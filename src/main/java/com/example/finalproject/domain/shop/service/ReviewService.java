package com.example.finalproject.domain.shop.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.shop.dto.request.ReviewRequestDto;
import com.example.finalproject.domain.shop.dto.response.ReviewResponseDto;
import com.example.finalproject.domain.shop.dto.response.ReviewStarResponseDto;
import com.example.finalproject.domain.shop.dto.response.ReviewpageResponseDto;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import com.example.finalproject.domain.shop.entity.ReviewLike;
import com.example.finalproject.domain.shop.exception.ReviewAuthorityException;
import com.example.finalproject.domain.shop.exception.ReviewImageLimitException;
import com.example.finalproject.domain.shop.repository.ReviewImageRepository;
import com.example.finalproject.domain.shop.repository.ReviewLikeRepository;
import com.example.finalproject.domain.shop.repository.ReviewRepository;
import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.PageResponse;
import com.example.finalproject.global.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;
import static com.example.finalproject.global.enums.SuccessCode.LIKE_CANCEL;
import static com.example.finalproject.global.enums.SuccessCode.LIKE_SUCCESS;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ShopService shopService;
    private final S3Utils s3Utils;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public SuccessCode createReview(String shopId, List<MultipartFile> multipartFile,
                                    ReviewRequestDto requestDto, UserDetailsImpl userDetails) {
        // image 등록 4개 제한
        if (multipartFile != null && multipartFile.size() > 4) {
            throw new ReviewImageLimitException(ErrorCode.LIMIT_MAX_IMAGE);
        }
        // shopId가 있는지 확인
        shopService.getSelectedShop(shopId, userDetails);
        Review review = new Review(requestDto, shopId, userDetails.getUser());
        // image가 없을 때 빈 url생성 방지
        if (s3Utils.isFile(multipartFile)) {
            uploadImg(multipartFile, review);
        }
        reviewRepository.save(review);
        return SuccessCode.COMMENT_CREATE_SUCCESS;
    }

    // 나중에 custom exception을 쓰게 되면 throw 문 지울것
    @Transactional
    public SuccessCode updateReview(String shopId, List<MultipartFile> multipartFile,
                                    Long reviewId, ReviewRequestDto requestDto, UserDetailsImpl userDetails) {
        // shopId가 있는지 확인
        shopService.getSelectedShop(shopId, userDetails);
        Review review = findReview(reviewId);
        checkAuthority(review, userDetails.getUser());
        review.update(requestDto);
        // image에 값이 있을때만 삭제 추가를 진행
        if (s3Utils.isFile(multipartFile)) {
            deleteImg(review);
            uploadImg(multipartFile, review);
        }
        return SuccessCode.COMMENT_UPDATE_SUCCESS;
    }

    public SuccessCode deleteReview(String shopId, Long reviewId, UserDetailsImpl userDetails) {
        // shopId가 있는지 확인
        shopService.getSelectedShop(shopId, userDetails);
        Review review = findReview(reviewId);
        checkAuthority(review, userDetails.getUser());
        deleteImg(review);
        reviewRepository.delete(review);
        return SuccessCode.COMMENT_DELETE_SUCCESS;
    }

    public ReviewStarResponseDto getStar(String shopId) {
        List<Review> reivewList = reviewRepository.findAllByShopId(shopId);
        int[] countStar = new int[6];
        reivewList.stream()
                .map(Review::getStar)
                .map((star) -> countStar[star]++);

        ReviewStarResponseDto reviewStarResponseDto = new ReviewStarResponseDto(countStar);
        return reviewStarResponseDto;
    }

    @Transactional
    public SuccessCode getlike(String shopId, Long reviewId, UserDetailsImpl userDetails) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        Optional<ReviewLike> reviewLike = reviewLikeRepository.findByReviewIdAndUserUserId(reviewId, userDetails.getUser().getUserId());
        if (reviewLike.isPresent()) {
            reviewLikeRepository.delete(reviewLike.get());
            return LIKE_CANCEL;
        }
        ReviewLike newReviewLike = reviewLikeRepository.save(new ReviewLike(userDetails.getUser(), review.getId(), shopId));
        review.getReviewLikes().add(newReviewLike);
        return LIKE_SUCCESS;
    }

    public ReviewpageResponseDto reviewList(int size, int page, UserDetailsImpl userDetails, String shopId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Review> reviewPage = reviewRepository.findByShopIdUsingQuery(shopId, pageable);

        List<Review> banner = reviewRepository.findAllByShopId(shopId);
        List<String> bannerList = shopService.getBanner(banner);
        Double avgStar = banner.stream()
                .mapToInt(Review::getStar)
                .average()
                .orElse(0);

        List<ReviewResponseDto> reviewList = new ArrayList<>();
        long total = reviewPage.getTotalElements();

        for (Review review : reviewPage) {
            Boolean is_like = getaBoolean(userDetails, review);
            Long like_count = reviewLikeRepository.countByReviewId(review.getId());
            ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review, like_count, is_like);
            reviewList.add(reviewResponseDto);
        }

        PageResponse pageResponse = new PageResponse<>(reviewList, pageable, total);
        return new ReviewpageResponseDto(pageResponse, bannerList, avgStar, banner.size());
    }

    /*------------------------------------------------------------------------------------------------------*/

    private void checkAuthority(Review review, User user) {
        // admin 확인
        if (!user.getRole().getAuthority().equals("ROLE_ADMIN")) {
            // userId 확인
            if (review.getUser().getUserId() != user.getUserId()) {
                throw new ReviewAuthorityException(ErrorCode.NO_AUTHORITY_TO_DATA);
            }
        }
    }

    private Review findReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new NullPointerException("존재하지 않는 후기입니다.")
        );
        return review;
    }

    private void deleteImg(Review review) {
        // 기존 review에서 이미지 삭제
        review.getImageUrls().forEach(reviewImageRepository::delete);
        review.getImageUrls().clear();
    }

    private void uploadImg(List<MultipartFile> multipartFile, Review review) {
        List<String> urls = s3Utils.uploadFile(multipartFile);
        for (String url : urls) {
            ReviewImage reviewImage = new ReviewImage(url);
            reviewImageRepository.save(reviewImage);
            review.getImageUrls().add(reviewImage);
        }
    }

    private Boolean getaBoolean(UserDetailsImpl userDetails, Review review) {
        Boolean is_like;
        if (userDetails == null) {
            is_like = false;
        } else {
            is_like = reviewLikeRepository.existsByReviewIdAndUserUserId(review.getId(), userDetails.getUser().getUserId());
        }
        return is_like;
    }
}

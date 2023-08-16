package com.example.finalproject.domain.shop.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.shop.dto.ReviewRequestDto;
import com.example.finalproject.domain.shop.dto.ReviewStarResponseDto;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import com.example.finalproject.domain.shop.exception.ReviewAuthorityException;
import com.example.finalproject.domain.shop.repository.ReviewImageRepository;
import com.example.finalproject.domain.shop.repository.ReviewRepository;
import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ShopService shopService;
    private final S3Utils s3Utils;
    private final ReviewImageRepository reviewImageRepository;
    private final UserRepository userRepository;
    @Transactional
    public SuccessCode createReview(String shopId, List<MultipartFile> multipartFile, ReviewRequestDto requestDto, User user) {
        // shopId가 있는지 확인
        shopService.getSelectedShop(shopId, user);
        Review review = new Review(requestDto, shopId, user);
        // image가 없을 때 빈 url생성 방지
        if(s3Utils.isFile(multipartFile)) {
            List<String> urls = s3Utils.uploadFile(multipartFile);
            for (String url : urls) {
                ReviewImage reviewImage = new ReviewImage(url);
                reviewImageRepository.save(reviewImage);
                review.getImageUrls().add(reviewImage);
            }
        }
        reviewRepository.save(review);
        return SuccessCode.COMMENT_CREATE_SUCCESS;
    }

    // 나중에 custom exception을 쓰게 되면 throw 문 지울것
    @Transactional
    public SuccessCode updateReview(String shopId, List<MultipartFile> multipartFile, Long reviewId, ReviewRequestDto requestDto, User user) {
        // shopId가 있는지 확인
        shopService.getSelectedShop(shopId, user);
        Review review = findReview(reviewId);
        checkAuthority(review, user);
        review.update(requestDto);
        // image에 값이 있을때만 삭제 추가를 진행
        if(s3Utils.isFile(multipartFile)) {
            // 기존 review에서 이미지 삭제
            review.getImageUrls().forEach(reviewImageRepository::delete);
            review.getImageUrls().clear();
            List<String> urls = s3Utils.uploadFile(multipartFile);
            for (String url : urls) {
                ReviewImage reviewImage = new ReviewImage(url);
                reviewImageRepository.save(reviewImage);
                review.getImageUrls().add(reviewImage);
            }
        }

        return SuccessCode.COMMENT_UPDATE_SUCCESS;
    }

    public SuccessCode deleteReview(String shopId, Long reviewId, User user) {
        // shopId가 있는지 확인
        shopService.getSelectedShop(shopId, user);
        Review review = findReview(reviewId);
        checkAuthority(review, user);
        // 기존 review에서 이미지 삭제
        review.getImageUrls().forEach(reviewImageRepository::delete);
        review.getImageUrls().clear();
        reviewRepository.delete(review);
        return SuccessCode.COMMENT_DELETE_SUCCESS;
    }


    public Review findReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new NullPointerException("존재하지 않는 후기입니다.")
        );
        return review;
    }

    public void checkAuthority(Review review, User user) {
        // admin 확인
        if (!user.getRole().getAuthority().equals("ROLE_ADMIN")) {
            // userId 확인
            if (review.getUser().getUserId() != user.getUserId()) {
                throw new ReviewAuthorityException(ErrorCode.NO_AUTHORITY_TO_DATA);
            }
        }
    }


    public ReviewStarResponseDto getStar(String shopId){
        List<Review> reivewList=reviewRepository.findAllByShopId(shopId);
        int[] countStar=new int[6];
        for(Review review : reivewList){
            Integer star=review.getStar();
            switch (star) {
                case 0:
                    countStar[0]++;
                    break;
                case 1:
                    countStar[1]++;
                    break;
                case 2:
                    countStar[2]++;
                    break;
                case 3:
                    countStar[3]++;
                    break;
                case 4:
                    countStar[4]++;
                    break;
                case 5:
                    countStar[5]++;
                    break;
            }
        }
        ReviewStarResponseDto reviewStarResponseDto=new ReviewStarResponseDto(countStar);

        return reviewStarResponseDto;
    }


}

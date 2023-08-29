package com.example.finalproject.domain.shop.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.shop.dto.*;
import com.example.finalproject.domain.shop.dto.response.*;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import com.example.finalproject.domain.shop.entity.Shop;
import com.example.finalproject.domain.shop.entity.ShopLike;
import com.example.finalproject.domain.shop.repository.ReviewLikeRepository;
import com.example.finalproject.domain.shop.repository.ReviewRepository;
import com.example.finalproject.domain.shop.repository.ShopLikeRepository;
import com.example.finalproject.domain.shop.repository.ShopRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.utils.GuestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j(topic = "openApi shop 조회")
@RequiredArgsConstructor
@Service
public class ShopService {
    private final ReviewRepository reviewRepository;
    private final ShopLikeRepository shopLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ShopRepository shopRepository;
    private final GuestUtil guestUtil;

    public ShopOneResponseDto getSelectedShop(String shopId, UserDetailsImpl userDetails) {
        log.info("특정 shop 상세 조회");
        User user = guestUtil.checkGuest(userDetails);
        return fromJSONtoShop(shopId, user);
    }

    public SuccessCode likeShop(String shopId, UserDetailsImpl userDetails) {
        log.info("특정 가게 좋아요");
        ShopLike shopLike = shopLikeRepository.findByShopIdAndUser(shopId, userDetails.getUser()).orElse(null);
        if (shopLike != null) {
            shopLikeRepository.delete(shopLike);
            return SuccessCode.LIKE_CANCEL;
        } else {
            shopLike = new ShopLike(shopId, userDetails.getUser());
            shopLikeRepository.save(shopLike);
            return SuccessCode.LIKE_SUCCESS;
        }
    }

    private ShopOneResponseDto fromJSONtoShop(String shopId, User user) {
        Shop shop = shopRepository.findByShopId(shopId);
        // 댓글 최신순
        CompletableFuture<List<Review>> reviewsFuture = CompletableFuture.supplyAsync(() -> {
            List<Review> reviews = reviewRepository.findAllByShopId(shopId);
            return reviews;
        });

        CompletableFuture<List<ShopLike>> shopLikesFuture = CompletableFuture.supplyAsync(() -> {
            List<ShopLike> shopLikes = shopLikeRepository.findAllByShopId(shopId);
            return shopLikes;
        });

// 모든 CompletableFuture 작업이 완료되었는지 확인
        CompletableFuture.allOf(reviewsFuture, shopLikesFuture).join();
        List<Review> reviews = reviewsFuture.join();
        List<ShopLike> shopLikes = shopLikesFuture.join();

        CompletableFuture<List<String>> bannerFuture = CompletableFuture.supplyAsync(() -> {
            List<String> result = getBanner(reviews);
            return result;
        });

        CompletableFuture<Integer> imageSizeFuture = CompletableFuture.supplyAsync(() -> {
            Integer size = getImageSize(reviews);
            return size;
        });

// 모든 CompletableFuture 작업이 완료되었는지 확인
        CompletableFuture.allOf(bannerFuture, imageSizeFuture).join();

        List<String> banner = bannerFuture.join();
        Integer reviewImageSize = imageSizeFuture.join();

        ShopOneResponseDto shopOneResponseDto = new ShopOneResponseDto(shop, reviews, shopLikes, user, banner, reviewImageSize);
        return shopOneResponseDto;
    }

    private Integer getImageSize(List<Review> reviews) {
        Integer count = 0;
        for (Review review : reviews) {
            count += review.getImageUrls().size();
        }
        return count;
    }

    public List<String> getBanner(List<Review> reviews) {
        int imageSize = 4;
        String defaultImageUrl = "https://finalimgbucket.s3.ap-northeast-2.amazonaws.com/63db46a0-b705-4af5-9e39-6cb56bbfe842";

        List<ShopBannerDto> bannerList = new ArrayList<>();

        for (Review review : reviews) {
            if (review.getImageUrls().isEmpty()) {
                continue;
            }
            Long count = reviewLikeRepository.countByReviewId(review.getId());
            List<String> imageList = review.getImageUrls()
                    .stream()
                    .map(ReviewImage::getImage)
                    .collect(Collectors.toList());

            bannerList.add(new ShopBannerDto(count, imageList));
        }

        // Sort by like count
        bannerList.sort((o1, o2) -> Long.compare(o2.getLikeCount(), o1.getLikeCount()));

        List<String> sortedImages = bannerList.stream()
                .flatMap(b -> b.getImgUrls().stream())
                .collect(Collectors.toList());

        // Fill up to imageSize
        while (sortedImages.size() < imageSize) {
            sortedImages.add(defaultImageUrl);
        }
        return sortedImages;
    }

    public ShopsPageResponseDto radiusSearch(Integer size, Integer page, String longitude, String latitude, UserDetailsImpl userDetails) {
        Double myLongitude = Double.valueOf(longitude);
        Double myLatitude = Double.valueOf(latitude);
        User user = guestUtil.checkGuest(userDetails);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Shop> shopPage = shopRepository.searchByCondition(myLongitude, myLatitude, 2000, pageable);

        List<ShopPageDto> shopPageDtoList = shopPage.get()
                .map(shop -> new ShopPageDto(shop, reviewRepository.findAllByShopId(shop.getShopId()),
                        shopLikeRepository.findAllByShopId(shop.getShopId()), user)).toList();
        ShopsPageResponseDto shopsResponseDto = new ShopsPageResponseDto(shopPage, shopPageDtoList);
        return shopsResponseDto;
    }
}
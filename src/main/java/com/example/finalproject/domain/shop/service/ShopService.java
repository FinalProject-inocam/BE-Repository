package com.example.finalproject.domain.shop.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.shop.dto.*;
import com.example.finalproject.domain.shop.dto.response.*;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import com.example.finalproject.domain.shop.entity.Shop;
import com.example.finalproject.domain.shop.entity.ShopLike;
import com.example.finalproject.domain.shop.exception.ShopNoContentException;
import com.example.finalproject.domain.shop.repository.ReviewLikeRepository;
import com.example.finalproject.domain.shop.repository.ReviewRepository;
import com.example.finalproject.domain.shop.repository.ShopLikeRepository;
import com.example.finalproject.domain.shop.repository.ShopRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.utils.GuestUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j(topic = "openApi shop 조회")
@Service
public class ShopService {

    private final RestTemplate restTemplate;
    private final ReviewRepository reviewRepository;
    private final ShopLikeRepository shopLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ShopRepository shopRepository;
//    private final QShopRepository qShopRepository;

    private final GuestUtil guestUtil;

    // RestTemplateBuilder의 build()를 사용하여 RestTemplate을 생성합니다.
    public ShopService(RestTemplateBuilder builder, ReviewRepository reviewRepository,
                       ShopLikeRepository shopLikeRepository, ReviewLikeRepository reviewLikeRepository,
                       ShopRepository shopRepository, GuestUtil guestUtil) {
        this.restTemplate = builder.build();
        this.reviewRepository = reviewRepository;
        this.shopLikeRepository = shopLikeRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.shopRepository = shopRepository;
        this.guestUtil = guestUtil;
//        this.qShopRepository = qShopRepository;
    }

    // openApi 사용시 필요한 servicekey
    @Value("${openApi.serviceKey}")
    private URI SERVICE_KEY;

    public ShopsResponseDto getShopList(String longitude, String latitude, UserDetailsImpl userDetails, Integer page, Integer size) {
        StopWatch stopWatch = new StopWatch("speed test");
        stopWatch.start("find shop");
        log.info("반경내의 shop 조회");
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString("http://apis.data.go.kr")
                .path("/B553077/api/open/sdsc2/storeListInRadius")
                .queryParam("pageNo", page)
                .queryParam("numOfRows", size)
                .queryParam("radius", 2000)
                .queryParam("cx", longitude)
                .queryParam("cy", latitude)
                .queryParam("indsSclsCd", "S20301")
                .queryParam("type", "json")
                .queryParam("serviceKey", SERVICE_KEY)
                .build()
                .toUriString());

        stopWatch.stop();

        stopWatch.start("entity");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        stopWatch.stop();

        System.out.println("실행 시간(ms): " + stopWatch.getTotalTimeMillis());
        System.out.println(stopWatch.prettyPrint());
        User user = guestUtil.checkGuest(userDetails);
        return fromJSONtoShopList(responseEntity.getBody(), user);
    }

    public ShopOneResponseDto getSelectedShop(String shopId, UserDetailsImpl userDetails) {
        log.info("특정 shop 상세 조회");
        User user = guestUtil.checkGuest(userDetails);
        return fromJSONtoShop(shopId, user);
    }

    public SuccessCode likeShop(String shopId, UserDetailsImpl userDetails) {
        log.info("특정 가게 좋아요");
        User user = guestUtil.checkGuest(userDetails);
        ShopLike shopLike = shopLikeRepository.findByShopIdAndUser(shopId, user).orElse(null);
        if (shopLike != null) {
            shopLikeRepository.delete(shopLike);
            return SuccessCode.LIKE_CANCEL;
        } else {
            shopLike = new ShopLike(shopId, user);
            shopLikeRepository.save(shopLike);
            return SuccessCode.LIKE_SUCCESS;
        }
    }

    private ShopsResponseDto fromJSONtoShopList(String responseEntity, User user) {
        try {
            JSONObject jsonObject = new JSONObject(responseEntity);

            JSONArray items = jsonObject.getJSONObject("body").getJSONArray("items");
            List<ShopDto> shopDtoList = new ArrayList<>();

            for (Object item : items) {
                JSONObject itemToJsonObj = (JSONObject) item;
                String shopId = itemToJsonObj.getString("bizesId");
                log.info(shopId);
                List<Review> reviews = reviewRepository.findAllByShopId(shopId);
                log.info(String.valueOf(reviews.size()));
                List<ShopLike> shopLikes = shopLikeRepository.findAllByShopId(shopId);
                ShopDto shopDto = new ShopDto(itemToJsonObj, reviews, shopLikes, user);
                shopDtoList.add(shopDto);
            }

            ShopsResponseDto shopsResponseDto = new ShopsResponseDto(jsonObject, shopDtoList);
            return shopsResponseDto;
        } catch (JSONException e) {
            throw new ShopNoContentException(SuccessCode.NO_SHOP_SUCCESS);
        }
    }

    private ShopOneResponseDto fromJSONtoShop(String shopId, User user) {

//        JSONObject jsonObject = new JSONObject(responseEntity);
//        JSONObject itemToJsonObj = jsonObject.getJSONObject("body")
//                .getJSONArray("items")
//                .getJSONObject(0);
//        String shopId = itemToJsonObj.getString("bizesId");
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
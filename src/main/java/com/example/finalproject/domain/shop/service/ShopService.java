package com.example.finalproject.domain.shop.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.dto.ShopDto;
import com.example.finalproject.domain.shop.dto.ShopOneResponseDto;
import com.example.finalproject.domain.shop.dto.ShopsResponseDto;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import com.example.finalproject.domain.shop.entity.ShopLike;
import com.example.finalproject.domain.shop.exception.ShopNoContentException;
import com.example.finalproject.domain.shop.repository.ReviewImageRepository;
import com.example.finalproject.domain.shop.repository.ReviewLikeRepository;
import com.example.finalproject.domain.shop.repository.ReviewRepository;
import com.example.finalproject.domain.shop.repository.ShopLikeRepository;
import com.example.finalproject.global.enums.SuccessCode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "openApi shop 조회")
@Service
public class ShopService {

    private final RestTemplate restTemplate;
    private final ReviewRepository reviewRepository;
    private final ShopLikeRepository shopLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    // RestTemplateBuilder의 build()를 사용하여 RestTemplate을 생성합니다.
    public ShopService(RestTemplateBuilder builder, ReviewRepository reviewRepository,
                       ShopLikeRepository shopLikeRepository, ReviewLikeRepository reviewLikeRepository) {
        this.restTemplate = builder.build();
        this.reviewRepository = reviewRepository;
        this.shopLikeRepository = shopLikeRepository;
        this.reviewLikeRepository = reviewLikeRepository;
    }

    // openApi 사용시 필요한 servicekey
    @Value("${openApi.serviceKey}")
    private URI SERVICE_KEY;

    public ShopsResponseDto getShopList(String longitude, String latitude, User user, Integer page, Integer size) {
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

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        return fromJSONtoShopList(responseEntity.getBody(), user);
    }

    public ShopOneResponseDto getSelectedShop(String shopId, User user) {
        log.info("특정 shop 상세 조회");
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString("http://apis.data.go.kr")
                .path("/B553077/api/open/sdsc2/storeOne")
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("key", shopId)
                .queryParam("type", "json")
                .build()
                .toUriString());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        return fromJSONtoShop(responseEntity.getBody(), user);
    }

    public SuccessCode likeShop(String shopId, User user) {
        log.info("특정 가게 좋아요");
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
                List<Review> reviews = reviewRepository.findAllByShopId(shopId);
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

    private ShopOneResponseDto fromJSONtoShop(String responseEntity, User user) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONObject itemToJsonObj = jsonObject.getJSONObject("body")
                .getJSONArray("items")
                .getJSONObject(0);

        String shopId = itemToJsonObj.getString("bizesId");
        // 댓글 최신순
        List<Review> reviews = reviewRepository.findAllByShopIdOrderByCreatedAtDesc(shopId);
        List<ShopLike> shopLikes = shopLikeRepository.findAllByShopId(shopId);
        List<String> banner = getBanner(shopId);
        Integer reviewImageSize = getImageSize(reviews);
        ShopOneResponseDto shopOneResponseDto = new ShopOneResponseDto(itemToJsonObj, reviews, shopLikes, user, banner, reviewImageSize);

        return shopOneResponseDto;
    }

    private Integer getImageSize(List<Review> reviews) {
        Integer count = 0;
        for (Review review : reviews) {
            count += review.getImageUrls().size();
        }
        return count;
    }

    public List<String> getBanner(String shopId) {
        // 좋아요 순서대로 정렬해서 가져오기<- 이거 리뷰에 좋아요하는 기능이 없어서 안됨 샵에 좋아요하는 기능이 있음
        // 그래서 일단 별점 순으로 정렬해서 가져옴
        List<Review> reviews = reviewRepository.findAllByShopIdOrderByStarDesc(shopId);

        Integer imageSize = 5;
        List<String> imageList = new ArrayList<>();
        reviewImageLoop:
        for (Review review : reviews) {
//            if (imageList.size() == imageSize) {
//                break;
//            }
            if (review.getImageUrls().size() == 0) {
                continue;
            }
//            Review review = reviews.get(i);
            List<ReviewImage> reviewImages = review.getImageUrls();
            for (ReviewImage reviewImage : reviewImages) {
                imageList.add(reviewImage.getImage());
                if (imageList.size() == imageSize) {
                    break reviewImageLoop;
                }
            }
//            imageList.add(review.getImageUrls().get(0).getImage());
        }
        while (imageList.size() < imageSize) {
            imageList.add("https://finalimgbucket.s3.ap-northeast-2.amazonaws.com/63db46a0-b705-4af5-9e39-6cb56bbfe842");
        }
        return imageList;
    }

}

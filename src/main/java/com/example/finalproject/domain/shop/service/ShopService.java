package com.example.finalproject.domain.shop.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.dto.ShopBannerDto;
import com.example.finalproject.domain.shop.dto.ShopDto;
import com.example.finalproject.domain.shop.dto.ShopOneResponseDto;
import com.example.finalproject.domain.shop.dto.ShopsResponseDto;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import com.example.finalproject.domain.shop.entity.ShopLike;
import com.example.finalproject.domain.shop.exception.ShopNoContentException;
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
import java.util.Collections;
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
        List<Review> reviews = reviewRepository.findAllByShopId(shopId);

        Integer imageSize = 4;
        List<String> imageList = new ArrayList<>();
        List<ShopBannerDto> bannerList = new ArrayList<>();

        for (Review review : reviews) { // reviews 리스트 각 Review 객체 순회
            if (review.getImageUrls().size() == 0) {    // review 객체 이미지 URL 목록 크기가 0인지 확인
                continue;   // 이미지 없으면 다음 리뷰 진행
            }
            Long count = reviewLikeRepository.countByReviewId(review.getId()); // 리뷰 개수 세알리기

            // 현재 리뷰 이미지 URL 목록 스트림 변환 후, 각 이미지 실제 이미지 데이터 매핑 후 imageList에 저장
            imageList = review.getImageUrls()
                    .stream()
                    .map(ReviewImage::getImage)
                    .toList();

            // 좋아요 개수를 ShopBannerDto 객체 생성
            ShopBannerDto shopBannerDto = new ShopBannerDto(count, imageList);
            // shopBannerDto 객체를 bannerList에 추가
            bannerList.add(shopBannerDto);
        }

        // 람다 표현식 사용해 o2 객체 좋아요 수와 o1 객체 좋아요 수 비교 후 정렬
        // 좋아요 개수가 높은 순서대로 정렬된 리스트 생성
        Collections.sort(bannerList, (o1, o2) -> Long.compare(o2.getLikeCount(), o1.getLikeCount()));

        // ArrayList 객체인 imageList 생성, 이미지 URL 저장하는 용도
        imageList = new ArrayList<>();
        // banner 객체 이미지 URL들을 순회하는 반복문 시작
        for (ShopBannerDto banner : bannerList) {
            for (String url : banner.getImgUrls()) {
                // url을 앞서 생성한 imageList에 추가한다. banner에 속한 이미지 URL들이 imageList 순차적 추가
                imageList.add(url);
            }
        }

        while (imageList.size() < imageSize) {  // imageList 크기를 imageSize와 같거나 크게 만드는 작업
            // 빈 공백 문자열 imageList에 추가해 이미지 URL이 없는 부분은 빈 문자열로 채움
            imageList.add("https://finalimgbucket.s3.ap-northeast-2.amazonaws.com/63db46a0-b705-4af5-9e39-6cb56bbfe842");
        }
        // 최종적으로 imageList 반환
        return imageList;
    }
}
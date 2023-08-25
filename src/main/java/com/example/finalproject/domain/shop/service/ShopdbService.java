package com.example.finalproject.domain.shop.service;

import com.example.finalproject.domain.shop.dto.ShopOneResponseDto;
import com.example.finalproject.domain.shop.entity.Shop;
import com.example.finalproject.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j(topic = "openApi shop 조회")
@Service
@RequiredArgsConstructor
public class ShopdbService {
    private final RestTemplate restTemplate;
    private final ShopRepository shopRepository;
    @Value("${openApi.serviceKey}")
    private URI SERVICE_KEY;

    public ShopOneResponseDto getSelectedShop() {
        log.info("특정 shop 상세 조회");
        for (int i = 38; i <= 39; i++) {
            URI uri = URI.create(UriComponentsBuilder
                    .fromUriString("http://apis.data.go.kr")
                    .path("/B553077/api/open/sdsc2/storeListInUpjong")
                    .queryParam("serviceKey", SERVICE_KEY)
                    .queryParam("key", "S20301")
                    .queryParam("pageNo", i)
                    .queryParam("numOfRows", 1000)
                    .queryParam("divId", "indsSclsCd")
                    .queryParam("type", "json")
                    .build()
                    .toUriString());
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            for (int j = 0; j <= 999; j++) {
                shopRepository.save(fromJSONtoShop(responseEntity.getBody(), j));
            }
            log.info("page : " + i);
        }
        return null;
    }

    private Shop fromJSONtoShop(String responseEntity, int i) {

        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONObject itemToJsonObj = jsonObject.getJSONObject("body")
                .getJSONArray("items")
                .getJSONObject(i);
        String shopId = itemToJsonObj.getString("bizesId");

        Shop shop = new Shop(itemToJsonObj);
        return shop;
    }
}

package com.example.finalproject.domain.shop.controller;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.shop.dto.ShopOneResponseDto;
import com.example.finalproject.domain.shop.dto.ShopsResponseDto;
import com.example.finalproject.domain.shop.service.ShopService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.google.protobuf.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "ShopController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public ApiResponse<?> getShops(String longitude, String latitude,
                                   Integer page, Integer size,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = checkGuest(userDetails);
        ShopsResponseDto shopsResponseDtos = shopService.getShopList(longitude, latitude, user, page, size);
        return ResponseUtils.ok(shopsResponseDtos);
    }

    @GetMapping("/{shopId}")
    public ApiResponse<?> getSelectedShop(@PathVariable String shopId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = checkGuest(userDetails);
        ShopOneResponseDto shopOneResponseDto = shopService.getSelectedShop(shopId, user);
        return ResponseUtils.ok(shopOneResponseDto);
    }

    @PatchMapping("{shopId}")
    public ApiResponse<?> likeShop(@PathVariable String shopId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = checkGuest(userDetails);
        SuccessCode successCode = shopService.likeShop(shopId, user);
        return ResponseUtils.ok(successCode);
    }

    private User checkGuest(UserDetailsImpl userDetails) {
        User user = null;
        try {
            user = userDetails.getUser();
        } catch (NullPointerException e) {
            log.info("게스트 사용자 입니다.");
        }
        return user;
    }
}
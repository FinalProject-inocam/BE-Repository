package com.example.finalproject.domain.shop.controller;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.shop.dto.ShopOneResponseDto;
import com.example.finalproject.domain.shop.dto.ShopsResponseDto;
import com.example.finalproject.domain.shop.service.ShopService;
import com.example.finalproject.domain.shop.service.ShopdbService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "ShopController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;
    private final ShopdbService shopdbService;

    @GetMapping
    public ApiResponse<?> getShops(String longitude, String latitude,
                                   Integer page, Integer size,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ShopsResponseDto shopsResponseDtos = shopService.getShopList(longitude, latitude, userDetails.getUser(), page, size);
        return ResponseUtils.pageOk(shopsResponseDtos.getSize(), shopsResponseDtos.getPage(),
                shopsResponseDtos.getTotalCount(), shopsResponseDtos.getTotalPages(), shopsResponseDtos.getShopList());
    }

    @GetMapping("/db")
    public ApiResponse<?> shopdb() {
        return ResponseUtils.ok(shopdbService.getSelectedShop());
    }

    @GetMapping("/{shopId}")
    public ApiResponse<?> getSelectedShop(@PathVariable String shopId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = checkGuest(userDetails);
        ShopOneResponseDto shopOneResponseDto = shopService.getSelectedShop(shopId, user);
        return ResponseUtils.ok(shopOneResponseDto);
    }

    @PatchMapping("/{shopId}")
    public ApiResponse<?> likeShop(@PathVariable String shopId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = checkGuest(userDetails);
        SuccessCode successCode = shopService.likeShop(shopId, user);
        return ResponseUtils.ok(successCode);
    }
}

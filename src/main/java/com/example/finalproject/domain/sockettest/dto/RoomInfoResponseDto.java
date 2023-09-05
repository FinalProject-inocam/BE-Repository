package com.example.finalproject.domain.sockettest.dto;

import com.example.finalproject.domain.mypage.dto.MypageResDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesResponseDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RoomInfoResponseDto {
    private UserInfoDto userInfo;
    private List<PurchaseResponseDtoSocket> userPurchaseList = new ArrayList<>();
    private String memo;

    public RoomInfoResponseDto(UserInfoDto userInfo, List<PurchaseResponseDtoSocket> userPurchaseList, String memoStr) {
        this.userInfo = userInfo;
        this.userPurchaseList = userPurchaseList;
        this.memo = memoStr;
    }
}

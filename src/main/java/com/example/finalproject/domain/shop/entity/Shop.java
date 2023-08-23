package com.example.finalproject.domain.shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Entity
@Getter
@NoArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String shopId;
    private String shopName;
    private String address;
    private Double longitude;
    private Double latitude;

    public Shop(JSONObject itemJson) {
        this.shopId = itemJson.getString("bizesId");
        this.shopName = itemJson.getString("bizesNm");
        this.address = itemJson.getString("rdnmAdr");
        if (this.address.isEmpty()) {
            this.address = itemJson.getString("lnoAdr");
        }
        this.longitude = itemJson.getDouble("lon");
        this.latitude = itemJson.getDouble("lat");
    }
}

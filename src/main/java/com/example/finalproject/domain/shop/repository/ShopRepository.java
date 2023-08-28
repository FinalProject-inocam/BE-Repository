package com.example.finalproject.domain.shop.repository;

import com.example.finalproject.domain.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Shop findByShopId(String shopId);


    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE FUNCTION('ST_Distance_Sphere', point(s.longitude, s.latitude), point(:longitude, :latitude)) < :distance")
    Page<Shop> searchByCondition(@Param("longitude") Double longitude,
                                 @Param("latitude") Double latitude,
                                 @Param("distance") int distance,
                                 Pageable pageable);

}

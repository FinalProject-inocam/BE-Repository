package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.purchases.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PurchasesRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findAll(Pageable pageable);

    List<Purchase> findAllByUser(User user);

    Page<Purchase> findByApprove(Pageable pageable, Boolean approve);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.approve IS NULL AND p.createdAt BETWEEN :startDate AND :endDate")
    Long countByApproveIsNullAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.approve = TRUE AND p.createdAt BETWEEN :startDate AND :endDate")
    Long countByApproveIsTrueAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.approve = FALSE AND p.createdAt BETWEEN :startDate AND :endDate")
    Long countByApproveIsFalseAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Long countByApprove(Boolean approve);

    /*--------------------------------------------------------------------------------------------------------------*/
    @Query("SELECT MONTH(p.createdAt) AS month, COUNT(*) AS count " +
            "FROM Purchase p " +
            "WHERE YEAR(p.createdAt) = :year " +  // 특정 년도
            "AND p.approve = :approve " +
            "GROUP BY MONTH(p.createdAt) " +
            "ORDER BY MONTH(p.createdAt)")
    List<Map<String, Object>> countPurchaseByYearAndApprove(int year, Boolean approve);

    @Query("SELECT MONTH(p.createdAt) AS month, COUNT(*) AS count " +
            "FROM Purchase p " +
            "WHERE YEAR(p.createdAt) = :year " +  // 특정 년도
            "AND p.type = :type " +                 // 특정 카테고리
            "AND (p.approve = :approve OR (:approve IS NULL AND p.approve is null)) " +
            "GROUP BY MONTH(p.createdAt) " +
            "ORDER BY MONTH(p.createdAt)")
    List<Map<String, Object>> countPurchaseByYearAndTypeAndApprove(int year, String type, Boolean approve);
    /* -------------------------------------------------------------------------------------------------------------- */

    List<Purchase> findTop2ByUserOrderByCreatedAtDesc(User user);

    List<Purchase> findAllByUserOrderByCreatedAtDesc(User user);

    List<Purchase> findTop2ByUserOrderByCreatedAt(User user);
}

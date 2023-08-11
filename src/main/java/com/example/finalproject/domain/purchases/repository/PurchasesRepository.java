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


    @Query("SELECT MONTH(y.createdAt) as month, COUNT(y) as count " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND (y.approve = :approve OR :approve IS NULL) " +
            "GROUP BY MONTH(y.createdAt)")
    List<Object[]> findMonthlyCountBetweenDates(@Param("startDateTime") LocalDateTime startDateTime,
                                                @Param("endDateTime") LocalDateTime endDateTime,
                                                @Param("approve") Boolean approve); // approve 파라미터 추가

    @Query("SELECT MONTH(y.createdAt) as month, COUNT(y) as count " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND y.approve IS NULL " +
            "GROUP BY MONTH(y.createdAt)")
    List<Object[]> findMonthlyCountWithoutApprove(@Param("startDateTime") LocalDateTime startDateTime,
                                                  @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT MONTH(y.createdAt) as month, COUNT(y) as count " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "(y.approve = :approve OR :approve IS NULL) AND " +
            "(y.type = :type OR :type IS NULL) " +
            "GROUP BY MONTH(y.createdAt)")
    List<Object[]> findTypeMonthlyCountBetweenDates(@Param("startDateTime") LocalDateTime startDateTime,
                                                    @Param("endDateTime") LocalDateTime endDateTime,
                                                    @Param("approve") Boolean approve,
                                                    @Param("type") String type);

    @Query("SELECT MONTH(y.createdAt) as month, COUNT(y) as count " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "(y.approve IS NULL) AND " +
            "(y.type = :type OR :type IS NULL) " +
            "GROUP BY MONTH(y.createdAt)")
    List<Object[]> findTypeMonthlyCountWithNullApprove(@Param("startDateTime") LocalDateTime startDateTime,
                                                       @Param("endDateTime") LocalDateTime endDateTime,
                                                       @Param("type") String type);

    @Query("SELECT COUNT(y) " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "y.approve = :approve")
    Long findAnnualCountBetweenDates(@Param("startDateTime") LocalDateTime startDateTime,
                                     @Param("endDateTime") LocalDateTime endDateTime,
                                     @Param("approve") Boolean approve);

    @Query("SELECT COUNT(y) " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "y.approve IS NULL")
    Long findAnnualCountWithoutApprove(@Param("startDateTime") LocalDateTime startDateTime,
                                       @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT COUNT(y) " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "(y.type = :type) AND " +
            "y.approve = :approve")
    Long findTypeAnnualCountBetweenDates(@Param("startDateTime") LocalDateTime startDateTime,
                                         @Param("endDateTime") LocalDateTime endDateTime,
                                         @Param("approve") Boolean approve,
                                         @Param("type") String type);

    @Query("SELECT COUNT(y) " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "(y.type = :type) AND " +
            "y.approve IS NULL")
    Long findTypeAnnualCountWithoutApprove(@Param("startDateTime") LocalDateTime startDateTime,
                                           @Param("endDateTime") LocalDateTime endDateTime,
                                           @Param("type") String type);

    //------------------------------------------getMonth
// 주별 전체 구매 수
    @Query("SELECT COUNT(p) " +
            "FROM Purchase p " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "AND p.approve IS NULL")
    Long findWeeklyCountWithoutApprove(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) " +
            "FROM Purchase p " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "AND p.approve = :approve")
    Long findTypeWeeklyCountByApprove(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("approve") Boolean approve);

    @Query("SELECT COUNT(y) " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "y.type = :type AND " +
            "y.approve IS NULL")
    Long findTypeWeeklyCountWithoutApprove(@Param("startDateTime") LocalDateTime startDateTime,
                                           @Param("endDateTime") LocalDateTime endDateTime,
                                           @Param("type") String type);

    @Query("SELECT COUNT(y) " +
            "FROM Purchase y " +
            "WHERE y.createdAt BETWEEN :startDateTime AND :endDateTime AND " +
            "y.type = :type AND " +
            "y.approve = :approve")
    Long findTypeWeeklyCountByApprove(@Param("startDateTime") LocalDateTime startDateTime,
                                      @Param("endDateTime") LocalDateTime endDateTime,
                                      @Param("type") String type,
                                      @Param("approve") Boolean approve);

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
}

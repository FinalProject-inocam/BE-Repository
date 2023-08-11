package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.domain.purchases.entity.QPurchase;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QPurchasesRepository {

    private final JPAQueryFactory queryFactory;

    public Map<Integer, Long> countPurchaseForYear(LocalDate localDate, Boolean approve, String type) {
        log.info("연간 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();

        BooleanExpression approveCondition = null;
        if (approve != null) {
            approveCondition = qPurchase.approve.eq(approve);
        }

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.createdAt.month(),
                        qPurchase.createdAt.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        approveCondition,
                        typeCondition
                )
                .groupBy(qPurchase.createdAt.month())
                .orderBy(qPurchase.createdAt.month().asc())
                .fetch();
        Map<Integer, Long> resultMap = new LinkedHashMap<>();  // 월별 데이터를 저장할 맵

        // 월별 데이터 초기화
        for (int i = 1; i <= 12; i++) {
            resultMap.put(i, 0L);  // 기본값 0으로 초기화
        }

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int month = tuple.get(qPurchase.createdAt.month());
            long count = tuple.get(qPurchase.createdAt.count());
            resultMap.put(month, count);
        }
        log.info("연간 통계 : " + resultMap);
        return resultMap;
    }

    public Long countPurchaseForPreYear(LocalDate localDate, Boolean approve, String type) {
        log.info("작년 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear() - 1;

        BooleanExpression approveCondition = null;
        if (approve != null) {
            approveCondition = qPurchase.approve.eq(approve);
        }

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Long> result = queryFactory
                .select(
                        qPurchase.createdAt.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        approveCondition,
                        typeCondition
                )
                .fetch();

        log.info("작년 통계 : " + result.toString());
        return result.get(0);
    }

    public Map<Integer, Long> countPurchaseForMonth(LocalDate localDate, Boolean approve, String type) {
        log.info("월간 통계");
        QPurchase qPurchase = QPurchase.purchase;

        YearMonth yearMonth = YearMonth.from(localDate);

        LocalDate startOfFirstWeek = yearMonth.atDay(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        Map<Integer, Long> resultMap = new LinkedHashMap<>();  // 주별 데이터를 저장할 맵

        BooleanExpression approveCondition = null;
        if (approve != null) {
            approveCondition = qPurchase.approve.eq(approve);
        }

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        // 각 주의 시작일을 계산하고 저장
        LocalDate currentWeekStart = startOfFirstWeek;
        int weekNumber = 1;

        while (!currentWeekStart.isAfter(yearMonth.atEndOfMonth())) {
            List<Long> result = queryFactory
                    .select(
                            qPurchase.createdAt.count()
                    )
                    .from(qPurchase)
                    .where(
                            qPurchase.createdAt.between(currentWeekStart.atStartOfDay(),
                                    currentWeekStart.plusDays(6).atTime(23, 59, 59, 999999)),
                            approveCondition,
                            typeCondition
                    )
                    .fetch();
            resultMap.put(weekNumber, result.get(0));

            currentWeekStart = currentWeekStart.plusDays(7);
            weekNumber++;
        }
        log.info("월간 통계 : " + resultMap);
        return resultMap;
    }

    public Long countPurchaseForPreMonth(LocalDate localDate, Boolean approve, String type) {
        log.info("전월 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();
        Integer month = localDate.getMonth().getValue() - 1;

        BooleanExpression approveCondition = null;
        if (approve != null) {
            approveCondition = qPurchase.approve.eq(approve);
        }

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Long> result = queryFactory
                .select(
                        qPurchase.createdAt.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        qPurchase.createdAt.month().eq(month),
                        approveCondition,
                        typeCondition
                )
                .fetch();

        log.info("전월 통계 : " + result.toString());
        return result.get(0);
    }

    public Map<Integer, Long> countPurchaseForWeek(LocalDate localDate, Boolean approve, String type) {
        log.info("주간 통계");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression approveCondition = null;
        if (approve != null) {
            approveCondition = qPurchase.approve.eq(approve);
        }

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.createdAt.dayOfWeek(),
                        qPurchase.createdAt.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.between(startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59, 999999)),
                        approveCondition,
                        typeCondition
                )
                .groupBy(qPurchase.createdAt.dayOfWeek())
                .orderBy(qPurchase.createdAt.dayOfWeek().asc())
                .fetch();

        Map<Integer, Long> resultMap = new LinkedHashMap<>();  // 주별 데이터를 저장할 맵

        // 일별 데이터 초기화
        for (int i = 1; i <= 7; i++) {
            resultMap.put(i, 0L);  // 기본값 0으로 초기화
        }
        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int day = tuple.get(qPurchase.createdAt.dayOfWeek());
            long count = tuple.get(qPurchase.createdAt.count());
            resultMap.put(day, count);
        }
        log.info("주간 통계 : " + resultMap);
        return resultMap;
    }

    public Long countPurchaseForPreWeek(LocalDate localDate, Boolean approve, String type) {
        log.info("전주 통계");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(1);
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).minusWeeks(1);

        BooleanExpression approveCondition = null;
        if (approve != null) {
            approveCondition = qPurchase.approve.eq(approve);
        }

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Long> result = queryFactory
                .select(
                        qPurchase.createdAt.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.between(startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59, 999999)),
                        approveCondition,
                        typeCondition
                )
                .fetch();

        log.info("전주 통계 : " + result.toString());
        return result.get(0);
    }

    //
//    public Map<Integer, Long> countPurchaseForWeek(LocalDate localDate, Boolean approve, String type) {
//        QPurchase qPurchase = QPurchase.purchase;
//
//        // localdate의 달의 첫째날의 주차를 빼서 맞추기
//        // WeekFields를 이용하여 주차 정보를 가져옵니다.
//        WeekFields weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1);
//        Integer weekNumber = localDate.get(weekFields.weekOfWeekBasedYear());
//        log.info(weekNumber.toString());
//
//        BooleanExpression approveCondition = null;
//        if (approve != null) {
//            approveCondition = qPurchase.approve.eq(approve);
//        }
//
//        BooleanExpression typeCondition = null;
//        if (type != null) {
//            typeCondition = qPurchase.type.eq(type);
//        }
//
//        List<Tuple> result0 = queryFactory
//                .select(
//                        qPurchase.createdAt.dayOfWeek(),
//                        qPurchase.createdAt,
//                        qPurchase.createdAt.count()
//                )
//                .from(qPurchase)
//                .where(
//                        qPurchase.createdAt.year().eq(localDate.getYear()),
//                        qPurchase.createdAt.week().eq(weekNumber - 1),
//                        qPurchase.createdAt.dayOfWeek().eq(1),
//                        approveCondition,
//                        typeCondition
//                )
//                .groupBy(qPurchase.createdAt.dayOfWeek())
//                .orderBy(qPurchase.createdAt.dayOfWeek().asc())
//                .fetch();
//
//        List<Tuple> result = queryFactory
//                .select(
//                        qPurchase.createdAt.dayOfWeek(),
//                        qPurchase.createdAt,
//                        qPurchase.createdAt.count()
//                )
//                .from(qPurchase)
//                .where(
//                        qPurchase.createdAt.year().eq(localDate.getYear()),
//                        qPurchase.createdAt.week().eq(weekNumber),
//                        approveCondition,
//                        typeCondition
//                )
//                .groupBy(qPurchase.createdAt.dayOfWeek())
//                .orderBy(qPurchase.createdAt.dayOfWeek().asc())
//                .fetch();
//        Map<Integer, Long> resultMap = new LinkedHashMap<>();  // 주별 데이터를 저장할 맵
//
//        // 일별 데이터 초기화
//        for (int i = 1; i <= 7; i++) {
//            resultMap.put(i, 0L);  // 기본값 0으로 초기화
//        }
//        StringBuilder sb = new StringBuilder();
//        // 결과를 맵에 저장
//        for (Tuple tuple : result) {
//            sb.append(tuple.get(qPurchase.createdAt).toString() + "//");
//            log.info(tuple.get(qPurchase.createdAt).toString());
//            // 현재 주차를 뺌
//            int day = tuple.get(qPurchase.createdAt.dayOfWeek());
//            long count = tuple.get(qPurchase.createdAt.count());
//            resultMap.put(day, count);
//        }
//
//        for (Tuple tuple : result0) {
//            sb.append(tuple.get(qPurchase.createdAt).toString() + "//");
//            log.info(tuple.get(qPurchase.createdAt).toString());
//            // 현재 주차를 뺌
//            int day = 1;
//            long count = tuple.get(qPurchase.createdAt.count());
//            resultMap.put(day, count);
//        }
//
//        log.info(sb.toString());
//        return resultMap;
//    }

//    public Map<Integer, Long> countPurchaseForMonth(LocalDate localDate, Boolean approve, String type) {
//        QPurchase qPurchase = QPurchase.purchase;
//        // localdate의 달의 첫째날의 주차를 빼서 맞추기
//        // WeekFields를 이용하여 주차 정보를 가져옵니다.
//        WeekFields weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1);
//        int weekNumber = localDate.get(weekFields.weekOfWeekBasedYear());
//
//        BooleanExpression approveCondition = null;
//        if (approve != null) {
//            approveCondition = qPurchase.approve.eq(approve);
//        }
//
//        BooleanExpression typeCondition = null;
//        if (type != null) {
//            typeCondition = qPurchase.type.eq(type);
//        }
//
//        List<Tuple> result = queryFactory
//                .select(
//                        qPurchase.createdAt.between(),
//                        qPurchase.createdAt.count()
//                )
//                .from(qPurchase)
//                .where(
//                        qPurchase.createdAt.year().eq(localDate.getYear()),
//                        qPurchase.createdAt.month().eq(localDate.getMonth().getValue()),
//                        approveCondition,
//                        typeCondition
//                )
//                .groupBy(qPurchase.createdAt.week())
//                .orderBy(qPurchase.createdAt.week().asc())
//                .fetch();
//        Map<Integer, Long> resultMap = new LinkedHashMap<>();  // 주별 데이터를 저장할 맵
//
//        // 주별 데이터 초기화
//        for (int i = 1; i <= 5; i++) {
//            resultMap.put(i, 0L);  // 기본값 0으로 초기화
//        }
//
//        // 결과를 맵에 저장
//        for (Tuple tuple : result) {
//            // 현재 주차를 뺌
//            int week = tuple.get(qPurchase.createdAt.week()) - weekNumber + 1;;
//            long count = tuple.get(qPurchase.createdAt.count());
//            resultMap.put(week, count);
//        }
//        return resultMap;
//    }


}

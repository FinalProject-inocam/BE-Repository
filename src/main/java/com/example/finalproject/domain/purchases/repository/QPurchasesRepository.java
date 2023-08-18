package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.domain.purchases.entity.QPurchase;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QPurchasesRepository {

    private final JPAQueryFactory queryFactory;

    public Map<Integer, Long> countPurchaseForYears(LocalDate StartDate, LocalDate endDate, Boolean approve, String type) {
        log.info("연별 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = StartDate.getYear();
        Integer endYear = endDate.getYear();

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
                        qPurchase.createdAt.year(),
                        qPurchase.createdAt.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().between(startYear, endYear),
                        approveCondition,
                        typeCondition
                )
                .groupBy(qPurchase.createdAt.year())
                .orderBy(qPurchase.createdAt.year().asc())
                .fetch();
        Map<Integer, Long> resultMap = new HashMap<>();  // 연별 데이터를 저장할 맵

        // 연별 데이터 초기화
        for (int i = startYear; i <= endYear; i++) {
            resultMap.put(i, 0L);  // 기본값 0으로 초기화
        }

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int year = tuple.get(qPurchase.createdAt.year());
            long count = tuple.get(qPurchase.createdAt.count());
            resultMap.put(year, count);
        }
        log.info("연별 통계 : " + resultMap);
        return resultMap;
    }

    public Map<String, Map> countPurchaseByGenderForYears(LocalDate StartDate, LocalDate endDate, String type) {
        log.info("다년간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = StartDate.getYear();
        Integer endYear = endDate.getYear();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.gender,
                        qPurchase.gender.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().between(startYear, endYear),
                        typeCondition
                )
                .groupBy(qPurchase.gender)
                .orderBy(qPurchase.gender.asc())
                .fetch();
        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("COMPANY", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender);
            long count = tuple.get(qPurchase.gender.count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("다년간 성별분포 : " + genderMap);
        return genderMap;
    }

    public Map<String, Map> countPurchaseByAgeForYears(LocalDate StartDate, LocalDate endDate, String type) {
        log.info("다년간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = StartDate.getYear();
        Integer endYear = endDate.getYear();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        NumberExpression<Integer> ageExpression = Expressions.currentDate().year()
                .subtract(qPurchase.birthYear)
                .divide(10).floor().multiply(10);

        List<Integer> result = queryFactory
                .select(
                        qPurchase.birthYear
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().between(startYear, endYear),
                        typeCondition
                )
                .fetch();

        Map<String, Map> ageMap = new HashMap<>();  // age 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        for (int i = 10; i < 80; i += 10) {
            resultMap.put(Integer.toString(i), 0l);
        }
        resultMap.put("70+", 0l);

        ageMap.put("byAge", resultMap);
        ageMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Integer birthYear : result) { // 사실 별로 안좋은 해결책인것 같은데...
            Integer age = (LocalDate.now().getYear() - birthYear) / 10 * 10;
            if (age > 60) {
                resultMap.put("70+", resultMap.get("70+") + 1);
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
            sum++;
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.round(resultMap.get(age) * 100 / sum));
        }

        log.info("다년간 나이분포 : " + ageMap);
        return ageMap;
    }

    public Map<String, Map> countPurchaseByColorForYears(LocalDate StartDate, LocalDate endDate, String type) {
        log.info("다년간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = StartDate.getYear();
        Integer endYear = endDate.getYear();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.color,
                        qPurchase.color.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().between(startYear, endYear),
                        typeCondition
                )
                .groupBy(qPurchase.color)
                .orderBy(qPurchase.color.asc())
                .fetch();
        Map<String, Map> colorMap = new HashMap<>();  // 색깔 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        colorMap.put("byColor", resultMap);
        colorMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String color = tuple.get(qPurchase.color);
            long count = tuple.get(qPurchase.color.count());
            resultMap.put(color, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("다년간 색깔분포 : " + colorMap);
        return colorMap;
    }

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
        Map<Integer, Long> resultMap = new HashMap<>();  // 월별 데이터를 저장할 맵

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

    public Map<String, Map> countPurchaseByGenderForYear(LocalDate localDate, String type) {
        log.info("연간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.gender,
                        qPurchase.gender.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        typeCondition
                )
                .groupBy(qPurchase.gender)
                .orderBy(qPurchase.gender.asc())
                .fetch();
        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("COMPANY", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender);
            long count = tuple.get(qPurchase.gender.count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("연간 성별분포 : " + genderMap);
        return genderMap;
    }

    public Map<String, Map> countPurchaseByAgeForYear(LocalDate localDate, String type) {
        log.info("연간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        NumberExpression<Integer> ageExpression = Expressions.currentDate().year()
                .subtract(qPurchase.birthYear)
                .divide(10).floor().multiply(10);

        List<Integer> result = queryFactory
                .select(
                        qPurchase.birthYear
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        typeCondition
                )
                .fetch();

        Map<String, Map> ageMap = new HashMap<>();  // age 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        for (int i = 10; i < 80; i += 10) {
            resultMap.put(Integer.toString(i), 0l);
        }
        resultMap.put("70+", 0l);

        ageMap.put("byAge", resultMap);
        ageMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Integer birthYear : result) { // 사실 별로 안좋은 해결책인것 같은데...
            Integer age = (LocalDate.now().getYear() - birthYear) / 10 * 10;
            if (age > 60) {
                resultMap.put("70+", resultMap.get("70+") + 1);
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
            sum++;
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.round(resultMap.get(age) * 100 / sum));
        }

        log.info("연간 나이분포 : " + ageMap);
        return ageMap;
    }

    public Map<String, Map> countPurchaseByColorForYear(LocalDate localDate, String type) {
        log.info("연간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.color,
                        qPurchase.color.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        typeCondition
                )
                .groupBy(qPurchase.color)
                .orderBy(qPurchase.color.asc())
                .fetch();
        Map<String, Map> colorMap = new HashMap<>();  // 색깔 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        colorMap.put("byColor", resultMap);
        colorMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String color = tuple.get(qPurchase.color);
            long count = tuple.get(qPurchase.color.count());
            resultMap.put(color, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("연간 색깔분포 : " + colorMap);
        return colorMap;
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

    public Map<String, Map> countPurchaseByGenderForMonth(LocalDate localDate, String type) {
        log.info("월간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();
        Integer month = localDate.getMonth().getValue();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        // 각 주의 시작일을 계산하고 저장
        List<Tuple> result = queryFactory
                .select(
                        qPurchase.gender,
                        qPurchase.gender.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        qPurchase.createdAt.month().eq(month),
                        typeCondition
                )
                .groupBy(qPurchase.gender)
                .fetch();

        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("COMPANY", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender);
            long count = tuple.get(qPurchase.gender.count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("월간 성별분포 : " + genderMap);
        return genderMap;
    }

    public Map<String, Map> countPurchaseByAgeForMonth(LocalDate localDate, String type) {
        log.info("월간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();
        Integer month = localDate.getMonth().getValue();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Integer> result = queryFactory
                .select(
                        qPurchase.birthYear
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        qPurchase.createdAt.month().eq(month),
                        typeCondition
                )
                .fetch();

        Map<String, Map> ageMap = new HashMap<>();  // age 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        for (int i = 10; i < 80; i += 10) {
            resultMap.put(Integer.toString(i), 0l);
        }
        resultMap.put("70+", 0l);

        ageMap.put("byAge", resultMap);
        ageMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Integer birthYear : result) { // 사실 별로 안좋은 해결책인것 같은데...
            Integer age = (LocalDate.now().getYear() - birthYear) / 10 * 10;
            if (age > 60) {
                resultMap.put("70+", resultMap.get("70+") + 1);
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
            sum++;
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.round(resultMap.get(age) * 100 / sum));
        }

        log.info("월간 나이분포 : " + ageMap);
        return ageMap;
    }

    public Map<String, Map> countPurchaseByColorForMonth(LocalDate localDate, String type) {
        log.info("월간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = localDate.getYear();
        Integer month = localDate.getMonth().getValue();

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.color,
                        qPurchase.color.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        qPurchase.createdAt.month().eq(month),
                        typeCondition
                )
                .groupBy(qPurchase.color)
                .orderBy(qPurchase.color.asc())
                .fetch();
        Map<String, Map> colorMap = new HashMap<>();  // 색깔 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        colorMap.put("byColor", resultMap);
        colorMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String color = tuple.get(qPurchase.color);
            long count = tuple.get(qPurchase.color.count());
            resultMap.put(color, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("월간 색깔분포 : " + colorMap);
        return colorMap;
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

    public Map<String, Map> countPurchaseByGenderForWeek(LocalDate localDate, String type) {
        log.info("주간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        // 각 주의 시작일을 계산하고 저장
        List<Tuple> result = queryFactory
                .select(
                        qPurchase.gender,
                        qPurchase.gender.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.between(startOfWeek.atStartOfDay(),
                                endOfWeek.atTime(23, 59, 59, 999999)),
                        typeCondition
                )
                .groupBy(qPurchase.gender)
                .fetch();

        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("COMPANY", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender);
            long count = tuple.get(qPurchase.gender.count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("주간 성별분포 : " + genderMap);
        return genderMap;
    }

    public Map<String, Map> countPurchaseByAgeForWeek(LocalDate localDate, String type) {
        log.info("주간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Integer> result = queryFactory
                .select(
                        qPurchase.birthYear
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.between(startOfWeek.atStartOfDay(),
                                endOfWeek.atTime(23, 59, 59, 999999)),
                        typeCondition
                )
                .fetch();

        Map<String, Map> ageMap = new HashMap<>();  // age 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        for (int i = 10; i < 80; i += 10) {
            resultMap.put(Integer.toString(i), 0l);
        }
        resultMap.put("70+", 0l);

        ageMap.put("byAge", resultMap);
        ageMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Integer birthYear : result) { // 사실 별로 안좋은 해결책인것 같은데...
            Integer age = (LocalDate.now().getYear() - birthYear) / 10 * 10;
            if (age > 60) {
                resultMap.put("70+", resultMap.get("70+") + 1);
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
            sum++;
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.round(resultMap.get(age) * 100 / sum));
        }

        log.info("주간 나이분포 : " + ageMap);
        return ageMap;
    }

    public Map<String, Map> countPurchaseByColorForWeek(LocalDate localDate, String type) {
        log.info("주간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.color,
                        qPurchase.color.count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.between(startOfWeek.atStartOfDay(),
                                endOfWeek.atTime(23, 59, 59, 999999)),
                        typeCondition
                )
                .groupBy(qPurchase.color)
                .orderBy(qPurchase.color.asc())
                .fetch();
        Map<String, Map> colorMap = new HashMap<>();  // 색깔 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        colorMap.put("byColor", resultMap);
        colorMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String color = tuple.get(qPurchase.color);
            long count = tuple.get(qPurchase.color.count());
            resultMap.put(color, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("주간 색깔분포 : " + colorMap);
        return colorMap;
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

}

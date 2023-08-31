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
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QPurchasesRepository {

    private final JPAQueryFactory queryFactory;

    public List<Object> countPurchaseForYears(String startYearStr, String endYearStr, Boolean approve, String type) {
        log.info("연별 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

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
        List<Object> labelResult = new ArrayList<>();
        List<Integer> label = new ArrayList<>();
        for (int i = startYear; i <= endYear; i++) {
            label.add(i);
        }

        int size = endYear - startYear + 1;
        List<Long> resultList = new ArrayList<>(Collections.nCopies(size, 0l));

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int year = tuple.get(qPurchase.createdAt.year());
            long count = tuple.get(qPurchase.createdAt.count());
            resultList.set(year - startYear, count);
        }
        log.info("연별 통계 : " + resultList.toString());

        labelResult.add(label);
        labelResult.add(resultList);

        return labelResult;
    }

    public List<Object> countPurchaseByGenderForYears(String startYearStr, String endYearStr, String type) {
        log.info("다년간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.gender.stringValue(),
                        qPurchase.gender.stringValue().count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().between(startYear, endYear),
                        typeCondition
                )
                .groupBy(qPurchase.gender.stringValue())
                .orderBy(qPurchase.gender.stringValue().asc())
                .fetch();
        List<Object> labelResultRatio = new ArrayList<>();  // gender, company 정보 담을 맵
        List<String> genderLabel = new ArrayList<>();
        genderLabel.add("MALE");
        genderLabel.add("FEMALE");
        List<Long> resultList = new ArrayList<>(Collections.nCopies(genderLabel.size(), 0l));
        List<Double> ratioList = new ArrayList<>(Collections.nCopies(genderLabel.size(), 0.0));

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender.stringValue());
            long count = tuple.get(qPurchase.gender.stringValue().count());
            resultList.set(genderLabel.indexOf(gender), count);
            sum += count;
        }

        for (int i = 0; i < genderLabel.size(); i++) {
            ratioList.set(i, Math.floor(resultList.get(i) * 1000 / sum) / 10.0);
        }
        labelResultRatio.add(genderLabel);
        labelResultRatio.add(resultList);
        labelResultRatio.add(ratioList);

        log.info("다년간 성별분포");
        return labelResultRatio;
    }

    public List<Object> countPurchaseByAgeForYears(String startYearStr, String endYearStr, String type) {
        log.info("다년간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

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
                        qPurchase.createdAt.year().between(startYear, endYear),
                        typeCondition
                )
                .fetch();

        List<Object> labelResultRatio = new ArrayList<>();
        List<String> ageLabel = new ArrayList<>();
        for (int i = 20; i < 80; i += 10) {
            ageLabel.add(Integer.toString(i));
        }
        ageLabel.add("70+");
        List<Long> resultList = new ArrayList<>(Collections.nCopies(ageLabel.size(), 0l));
        List<Double> ratioList = new ArrayList<>(Collections.nCopies(ageLabel.size(), 0.0));

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Integer birthYear : result) { // 사실 별로 안좋은 해결책인것 같은데...
            Integer age = (LocalDate.now().getYear() - birthYear) / 10 * 10;
            if (age > 60) {
                Integer over70 = resultList.size() - 1;
                resultList.set(over70, resultList.get(over70) + 1);
            }
            if (age < 20) {
                resultList.set(0, resultList.get(0) + 1);
            }
            String ageToString = Integer.toString(age);
            Integer ageLabelIndex = ageLabel.indexOf(ageToString);
            resultList.set(ageLabelIndex, resultList.get(ageLabelIndex) + 1);
            sum++;
        }

        for (int i = 0; i < resultList.size(); i++) {
            ratioList.set(i, Math.floor(resultList.get(i) * 1000 / sum) / 10.0);
        }

        labelResultRatio.add(resultList);
        labelResultRatio.add(ratioList);

        log.info("다년간 나이분포 : ");
        return labelResultRatio;
    }

    public Map<String, Map> countPurchaseByColorForYears(String startYearStr, String endYearStr, String type) {
        log.info("다년간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

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
        List<Object> labelResultRatio = new ArrayList<>();
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Double> ratioMap = new HashMap<>();

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
            ratioMap.put(gender, Math.floor(resultMap.get(gender) * 1000 / sum) / 10.0);
        }

        log.info("다년간 색깔분포 : " + colorMap);
        return colorMap;
    }

    public List<Long> countPurchaseForYear(String year, Boolean approve, String type) {
        log.info("연간 통계");
        QPurchase qPurchase = QPurchase.purchase;

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
                        qPurchase.createdAt.year().eq(Integer.valueOf(year)),
                        approveCondition,
                        typeCondition
                )
                .groupBy(qPurchase.createdAt.month())
                .orderBy(qPurchase.createdAt.month().asc())
                .fetch();
//        Map<Integer, Long> resultMap = new HashMap<>();  // 월별 데이터를 저장할 맵
        List<Long> resultList = new ArrayList<>(Collections.nCopies(12, 0l));

//        // 월별 데이터 초기화
//        for (int i = 1; i <= 12; i++) {
//            resultMap.put(i, 0L);  // 기본값 0으로 초기화
//        }

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int month = tuple.get(qPurchase.createdAt.month());
            long count = tuple.get(qPurchase.createdAt.count());
            resultList.set(month - 1, count);
        }
        log.info("연간 통계 : " + resultList.toString());
        return resultList;
    }

    public Map<String, Map> countPurchaseByGenderForYear(String year, String type) {
        log.info("연간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        List<Tuple> result = queryFactory
                .select(
                        qPurchase.gender.stringValue(),
                        qPurchase.gender.stringValue().count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(Integer.valueOf(year)),
                        typeCondition
                )
                .groupBy(qPurchase.gender.stringValue())
                .orderBy(qPurchase.gender.stringValue().asc())
                .fetch();
        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Double> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("COMPANY", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender.stringValue());
            long count = tuple.get(qPurchase.gender.stringValue().count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.floor(resultMap.get(gender) * 1000 / sum) / 10.0);
        }

        log.info("연간 성별분포 : " + genderMap);
        return genderMap;
    }

    public Map<String, Map> countPurchaseByAgeForYear(String year, String type) {
        log.info("연간 나이");
        QPurchase qPurchase = QPurchase.purchase;

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
                        qPurchase.createdAt.year().eq(Integer.valueOf(year)),
                        typeCondition
                )
                .fetch();

        Map<String, Map> ageMap = new HashMap<>();  // age 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Double> ratioMap = new HashMap<>();

        for (int i = 20; i < 80; i += 10) {
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
            if (age < 20) {
                resultMap.put("20", resultMap.get("20") + 1);
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
            sum++;
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.floor(resultMap.get(age) * 1000 / sum) / 10.0);
        }

        log.info("연간 나이분포 : " + ageMap);
        return ageMap;
    }

    public Map<String, Map> countPurchaseByColorForYear(String year, String type) {
        log.info("연간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

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
                        qPurchase.createdAt.year().eq(Integer.valueOf(year)),
                        typeCondition
                )
                .groupBy(qPurchase.color)
                .orderBy(qPurchase.color.asc())
                .fetch();
        Map<String, Map> colorMap = new HashMap<>();  // 색깔 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Double> ratioMap = new HashMap<>();

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
            ratioMap.put(gender, Math.floor(resultMap.get(gender) * 1000 / sum) / 10.0);
        }

        log.info("연간 색깔분포 : " + colorMap);
        return colorMap;
    }

    public Long countPurchaseForPreYear(String year, Boolean approve, String type) {
        log.info("작년 통계");
        QPurchase qPurchase = QPurchase.purchase;

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
                        qPurchase.createdAt.year().eq(Integer.valueOf(year) - 1),
                        approveCondition,
                        typeCondition
                )
                .fetch();

        log.info("작년 통계 : " + result.toString());
        return result.get(0);
    }

    public List<Long> countPurchaseForMonth(String yearMonthStr, Boolean approve, String type) {
        log.info("월간 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonthStr.split("-")[0]);
        Integer month = Integer.valueOf(yearMonthStr.split("-")[1]);

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startOfFirstWeek = yearMonth.atDay(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        List<Long> resultList = new ArrayList<>(Collections.nCopies(5, 0l));

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
        int weekNumber = 0;

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
            resultList.set(weekNumber, result.get(0));

            currentWeekStart = currentWeekStart.plusDays(7);
            weekNumber++;
        }
        if (weekNumber < 6) {
            resultList.remove(resultList.size() - 1);
        }
        log.info("월간 통계 : " + resultList.toString());
        return resultList;
    }

    public Map<String, Map> countPurchaseByGenderForMonth(String yearMonth, String type) {
        log.info("월간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]);

        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }

        // 각 주의 시작일을 계산하고 저장
        List<Tuple> result = queryFactory
                .select(
                        qPurchase.gender.stringValue(),
                        qPurchase.gender.stringValue().count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.year().eq(year),
                        qPurchase.createdAt.month().eq(month),
                        typeCondition
                )
                .groupBy(qPurchase.gender.stringValue())
                .fetch();

        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Double> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("COMPANY", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender.stringValue());
            long count = tuple.get(qPurchase.gender.stringValue().count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.floor(resultMap.get(gender) * 1000 / sum) / 10.0);
        }

        log.info("월간 성별분포 : " + genderMap);
        return genderMap;
    }

    public Map<String, Map> countPurchaseByAgeForMonth(String yearMonth, String type) {
        log.info("월간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]);

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
        Map<String, Double> ratioMap = new HashMap<>();

        for (int i = 20; i < 80; i += 10) {
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
            if (age < 20) {
                resultMap.put("20", resultMap.get("20") + 1);
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
            sum++;
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.floor(resultMap.get(age) * 1000 / sum) / 10.0);
        }

        log.info("월간 나이분포 : " + ageMap);
        return ageMap;
    }

    public Map<String, Map> countPurchaseByColorForMonth(String yearMonth, String type) {
        log.info("월간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]);

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
        Map<String, Double> ratioMap = new HashMap<>();

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
            ratioMap.put(gender, Math.floor(resultMap.get(gender) * 1000 / sum) / 10.0);
        }

        log.info("월간 색깔분포 : " + colorMap);
        return colorMap;
    }

    public Long countPurchaseForPreMonth(String yearMonth, Boolean approve, String type) {
        log.info("전월 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]) - 1;

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

    public List<Long> countPurchaseForWeek(LocalDate localDate, Boolean approve, String type) {
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

//        Map<Integer, Long> resultMap = new LinkedHashMap<>();  // 주별 데이터를 저장할 맵
        List<Long> resultList = new ArrayList<>(Collections.nCopies(7, 0l));


        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int day = tuple.get(qPurchase.createdAt.dayOfWeek());
            long count = tuple.get(qPurchase.createdAt.count());
            resultList.set(day - 1, count);
        }
        log.info("주간 통계 : " + resultList.toString());
        return resultList;
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
                        qPurchase.gender.stringValue(),
                        qPurchase.gender.stringValue().count()
                )
                .from(qPurchase)
                .where(
                        qPurchase.createdAt.between(startOfWeek.atStartOfDay(),
                                endOfWeek.atTime(23, 59, 59, 999999)),
                        typeCondition
                )
                .groupBy(qPurchase.gender.stringValue())
                .fetch();

        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Double> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("COMPANY", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String gender = tuple.get(qPurchase.gender.stringValue());
            long count = tuple.get(qPurchase.gender.stringValue().count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.floor(resultMap.get(gender) * 1000 / sum) / 10.0);
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
        Map<String, Double> ratioMap = new HashMap<>();

        for (int i = 20; i < 80; i += 10) {
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
            if (age < 20) {
                resultMap.put("20", resultMap.get("20") + 1);
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
            sum++;
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.floor(resultMap.get(age) * 1000 / sum) / 10.0);
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
        Map<String, Double> ratioMap = new HashMap<>();

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
            ratioMap.put(gender, Math.floor(resultMap.get(gender) * 1000 / sum) / 10.0);
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

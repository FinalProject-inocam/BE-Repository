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

    public Map<String, Object> countPurchaseForYears(String startYearStr, String endYearStr, Boolean approve, String type) {
        log.info("연별 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

        BooleanExpression approveCondition = approveCondition(approve, qPurchase);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        Map<String, Object> yearsResult = new HashMap<>();
        List<String> label = new ArrayList<>();
        for (int i = startYear; i <= endYear; i++) {
            label.add(i + "년");
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

        yearsResult.put("labels", label);
        yearsResult.put("values", resultList);

        return yearsResult;
    }



    public Map<String, Object> countPurchaseByGenderForYears(String startYearStr, String endYearStr, String type) {
        log.info("다년간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        return genderMap(qPurchase, result);
    }

    public Map<String, Object> countPurchaseByAgeForYears(String startYearStr, String endYearStr, String type) {
        log.info("다년간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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
        return ageMap(result);
    }

    public Map<String, Object> countPurchaseByColorForYears(String startYearStr, String endYearStr, String type) {
        log.info("다년간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer startYear = Integer.valueOf(startYearStr);
        Integer endYear = Integer.valueOf(endYearStr);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        return colorMap(qPurchase, result);
    }

    public Map<String, Object> countPurchaseForYear(String year, Boolean approve, String type) {
        log.info("연간 통계");
        QPurchase qPurchase = QPurchase.purchase;

        BooleanExpression approveCondition = approveCondition(approve, qPurchase);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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
        Map<String, Object> yearResult = new HashMap<>();  // 월별 데이터를 저장할 맵
        List<String> monthLabel = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            monthLabel.add(i + "월");
        }
        List<Long> resultList = new ArrayList<>(Collections.nCopies(monthLabel.size(), 0l));

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int month = tuple.get(qPurchase.createdAt.month());
            long count = tuple.get(qPurchase.createdAt.count());
            resultList.set(month - 1, count);
        }

        yearResult.put("labels", monthLabel);
        yearResult.put("values", resultList);

        return yearResult;
    }

    public Map<String, Object> countPurchaseByGenderForYear(String year, String type) {
        log.info("연간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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
        return genderMap(qPurchase, result);
    }

    public Map<String, Object> countPurchaseByAgeForYear(String year, String type) {
        log.info("연간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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
        return ageMap(result);
    }

    public Map<String, Object> countPurchaseByColorForYear(String year, String type) {
        log.info("연간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        return colorMap(qPurchase, result);
    }

    public Long countPurchaseForPreYear(String year, Boolean approve, String type) {
        log.info("작년 통계");
        QPurchase qPurchase = QPurchase.purchase;

        BooleanExpression approveCondition = approveCondition(approve, qPurchase);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

    public Map<String, Object> countPurchaseForMonth(String yearMonthStr, Boolean approve, String type) {
        log.info("월간 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonthStr.split("-")[0]);
        Integer month = Integer.valueOf(yearMonthStr.split("-")[1]);

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startOfFirstWeek = yearMonth.atDay(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        Map<String, Object> monthMap = new HashMap<>();
        List<String> weekLabel = new ArrayList<>();
        List<Long> resultList = new ArrayList<>(Collections.nCopies(6, 0l));

        BooleanExpression approveCondition = approveCondition(approve, qPurchase);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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
            weekLabel.add(weekNumber + "주차");
            resultList.set(weekNumber -1, result.get(0));

            currentWeekStart = currentWeekStart.plusDays(7);
            weekNumber++;
        }
        resultList.remove(weekNumber - 2);

        monthMap.put("labels", weekLabel);
        monthMap.put("values", resultList);

        return monthMap;
    }

    public Map<String, Object> countPurchaseByGenderForMonth(String yearMonth, String type) {
        log.info("월간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        return genderMap(qPurchase, result);
    }

    public Map<String, Object> countPurchaseByAgeForMonth(String yearMonth, String type) {
        log.info("월간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        return ageMap(result);
    }

    public Map<String, Object> countPurchaseByColorForMonth(String yearMonth, String type) {
        log.info("월간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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
        return colorMap(qPurchase, result);
    }

    public Long countPurchaseForPreMonth(String yearMonth, Boolean approve, String type) {
        log.info("전월 통계");
        QPurchase qPurchase = QPurchase.purchase;

        Integer year = Integer.valueOf(yearMonth.split("-")[0]);
        Integer month = Integer.valueOf(yearMonth.split("-")[1]) - 1;

        BooleanExpression approveCondition = approveCondition(approve, qPurchase);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

    public Map<String, Object> countPurchaseForWeek(LocalDate localDate, Boolean approve, String type) {
        log.info("주간 통계");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression approveCondition = approveCondition(approve, qPurchase);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        Map<String, Object> resultMap = new HashMap<>();  // 주별 데이터를 저장할 맵
        List<String> weeklLabel = new ArrayList<>();
        weeklLabel.add("SUN");
        weeklLabel.add("MON");
        weeklLabel.add("TUE");
        weeklLabel.add("WED");
        weeklLabel.add("THU");
        weeklLabel.add("FRI");
        weeklLabel.add("SAT");

        List<Long> resultList = new ArrayList<>(Collections.nCopies(weeklLabel.size(), 0l));

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int day = tuple.get(qPurchase.createdAt.dayOfWeek());
            long count = tuple.get(qPurchase.createdAt.count());
            resultList.set(day - 1, count);
        }

        resultMap.put("labels", weeklLabel);
        resultMap.put("values", resultList);

        return resultMap;
    }

    public Map<String, Object> countPurchaseByGenderForWeek(LocalDate localDate, String type) {
        log.info("주간 성별분포");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        return genderMap(qPurchase, result);
    }

    public Map<String, Object> countPurchaseByAgeForWeek(LocalDate localDate, String type) {
        log.info("주간 나이");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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

        return ageMap(result);
    }

    public Map<String, Object> countPurchaseByColorForWeek(LocalDate localDate, String type) {
        log.info("주간 색깔분포");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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
        return colorMap(qPurchase, result);
    }

    public Long countPurchaseForPreWeek(LocalDate localDate, Boolean approve, String type) {
        log.info("전주 통계");
        QPurchase qPurchase = QPurchase.purchase;

        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(1);
        LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).minusWeeks(1);

        BooleanExpression approveCondition = approveCondition(approve, qPurchase);

        BooleanExpression typeCondition = getTypeCondition(type, qPurchase);

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


    /*---------------------------------------------------------*/

    private static BooleanExpression approveCondition(Boolean approve, QPurchase qPurchase) {
        BooleanExpression approveCondition = null;
        if (approve != null) {
            approveCondition = qPurchase.approve.eq(approve);
        }
        return approveCondition;
    }

    private static BooleanExpression getTypeCondition(String type, QPurchase qPurchase) {
        BooleanExpression typeCondition = null;
        if (type != null) {
            typeCondition = qPurchase.type.eq(type);
        }
        return typeCondition;
    }

    private static Map<String, Object> genderMap(QPurchase qPurchase, List<Tuple> result) {
        Map<String, Object> genderMap = new HashMap<>();
        List<String> genderLabel = new ArrayList<>();
        genderLabel.add("MALE");
        genderLabel.add("FEMALE");
        genderLabel.add("COMPANY");

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

        genderMap.put("labels", genderLabel);
        genderMap.put("values", resultList);
        genderMap.put("ratios", ratioList);

        return genderMap;
    }

    private static Map<String, Object> ageMap(List<Integer> result) {
        Map<String, Object> ageMap = new HashMap<>();
        List<String> ageLabel = new ArrayList<>();
        ageLabel.add("10~20대");
        for (int i = 30; i < 70; i += 10) {
            ageLabel.add(i + "대");
        }
        ageLabel.add("70대 이상");

        List<Long> resultList = new ArrayList<>(Collections.nCopies(ageLabel.size(), 0l));
        List<Double> ratioList = new ArrayList<>(Collections.nCopies(ageLabel.size(), 0.0));

        Float sum = 0f;

        Integer over70 = ageLabel.indexOf("70대 이상");
        Integer under20 = ageLabel.indexOf("10~20대");

        // 결과를 맵에 저장
        for (Integer birthYear : result) { // 사실 별로 안좋은 해결책인것 같은데...
            Integer age = (LocalDate.now().getYear() - birthYear) / 10 * 10;
            if (age > 60) {
                resultList.set(over70, resultList.get(over70) + 1);
                sum++;
                continue;
            }
            if (age < 30) {
                resultList.set(0, resultList.get(under20) + 1);
                sum++;
                continue;
            }
            Integer ageLabelIndex = ageLabel.indexOf(age + "대");
            resultList.set(ageLabelIndex, resultList.get(ageLabelIndex) + 1);
            sum++;
        }

        for (int i = 0; i < resultList.size(); i++) {
            ratioList.set(i, Math.floor(resultList.get(i) * 1000 / sum) / 10.0);
        }
        ageMap.put("labels", ageLabel);
        ageMap.put("values", resultList);
        ageMap.put("ratios", ratioList);

        return ageMap;
    }

    private static Map<String, Object> colorMap(QPurchase qPurchase, List<Tuple> result) {
        Map<String, Object> colorMap = new HashMap<>();
        List<String> colorLabel = new ArrayList<>();
        colorLabel.add("black");
        colorLabel.add("white");
        colorLabel.add("yellow");
        colorLabel.add("blue");

        List<Long> resultList = new ArrayList<>(Collections.nCopies(colorLabel.size(), 0l));
        List<Double> ratioList = new ArrayList<>(Collections.nCopies(colorLabel.size(), 0.0));

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            String color = tuple.get(qPurchase.color);
            long count = tuple.get(qPurchase.color.count());
            resultList.set(colorLabel.indexOf(color), count);
            sum += count;
        }

        for (int i = 0; i < resultList.size(); i++) {
            ratioList.set(i, Math.floor(resultList.get(i) * 1000 / sum) / 10.0);
        }

        colorMap.put("labels", colorLabel);
        colorMap.put("values", resultList);
        colorMap.put("ratios", ratioList);

        return colorMap;
    }
}

package com.example.finalproject.domain.auth.repository;

import com.example.finalproject.domain.auth.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QUserRepository {

    private final JPAQueryFactory queryFactory;

    // 현재 createdAt없음
    public Map<Integer, Long> countUserForYear(LocalDate localDate) {
        log.info("연간 회원 통계");
        QUser qUser = QUser.user;

        Integer year = localDate.getYear();

        List<Tuple> result = queryFactory
                .select(
                        qUser.createdAt.month(),
                        qUser.createdAt.count()
                )
                .from(qUser)
                .where(
                        qUser.createdAt.year().eq(year)
                        )
                .groupBy(qUser.createdAt.month())
                .orderBy(qUser.createdAt.month().asc())
                .fetch();
        Map<Integer, Long> resultMap = new HashMap<>();  // 월별 데이터를 저장할 맵

        // 월별 데이터 초기화
        for (int i = 1; i <= 12; i++) {
            resultMap.put(i, 0L);  // 기본값 0으로 초기화
        }

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            int month = tuple.get(qUser.createdAt.month());
            long count = tuple.get(qUser.createdAt.count());
            resultMap.put(month, count);
        }
        log.info("연간 회원 통계 : " + resultMap);
        return resultMap;
    }

    public Map<String, Map> countUserByGenderForYear(LocalDate localDate) {
        log.info("연간 회원 성별분포");
        QUser qUser = QUser.user;

        Integer year = localDate.getYear();

        List<Tuple> result = queryFactory
                .select(
                        qUser.gender.stringValue().coalesce("UNKNOWN"),
                        qUser.gender.stringValue().coalesce("UNKNOWN").count()
                )
                .from(qUser)
                .where(
                        qUser.createdAt.year().eq(year)
                )
                .groupBy(qUser.gender.stringValue().coalesce("UNKNOWN"))
                .orderBy(qUser.gender.stringValue().coalesce("UNKNOWN").asc())
                .fetch();
        Map<String, Map> genderMap = new HashMap<>();  // gender, company 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        resultMap.put("MALE", 0l);
        resultMap.put("FEMALE", 0l);
        resultMap.put("UNKNOWN", 0l);

        genderMap.put("byGender", resultMap);
        genderMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Tuple tuple : result) {
            // get시에 null값이 문제가 될것 같은데...
            String gender = tuple.get(qUser.gender.stringValue().coalesce("UNKNOWN"));
            Long count = tuple.get(qUser.gender.stringValue().coalesce("UNKNOWN").count());
            resultMap.put(gender, count);
            sum += count;
        }

        for (String gender : resultMap.keySet()) {
            ratioMap.put(gender, Math.round(resultMap.get(gender) * 100 / sum));
        }

        log.info("연간 회원 성별분포 : " + genderMap);
        return genderMap;
    }

    public Map<String, Map> countUserByAgeForYear(LocalDate localDate) {
        log.info("연간 회원 나이대");
        QUser qUser = QUser.user;

        Integer year = localDate.getYear();

        NumberExpression<Integer> ageExpression = Expressions.currentDate().year()
                .subtract(qUser.birthYear)
                .divide(10).floor().multiply(10);

        List<Integer> result = queryFactory
                .select(
                        qUser.birthYear
                                .coalesce(0)
                )
                .from(qUser)
                .where(
                        qUser.createdAt.year().eq(year)
                        )
                .fetch();

        Map<String, Map> ageMap = new HashMap<>();  // age 정보 담을 맵
        Map<String, Long> resultMap = new HashMap<>();
        Map<String, Integer> ratioMap = new HashMap<>();

        for (int i = 20; i < 70; i += 10) {
            resultMap.put(Integer.toString(i), 0l);
        }
        resultMap.put("10-", 0l);
        resultMap.put("70+", 0l);
        resultMap.put("unknown", 0l);

        ageMap.put("byAge", resultMap);
        ageMap.put("ratio", ratioMap);

        Float sum = 0f;

        // 결과를 맵에 저장
        for (Integer birthYear : result) { // 사실 별로 안좋은 해결책인것 같은데...
            Integer age = (LocalDate.now().getYear() - birthYear) / 10 * 10;
            sum++;
            if (age > 1000) {
                Long value = resultMap.get("unknown");
                resultMap.put("unknown", value + 1);
                continue;
            }
            if (age > 60) {
                Long value = resultMap.get("70+");
                resultMap.put("70+", value + 1);
                continue;
            }
            if (age < 20) {
                Long value = resultMap.get("10-");
                resultMap.put("10-", value + 1);
                continue;
            }
            String ageToString = Integer.toString(age);
            resultMap.put(ageToString, resultMap.get(ageToString) + 1);
        }

        for (String age : resultMap.keySet()) {
            ratioMap.put(age, Math.round(resultMap.get(age) * 100 / sum));
        }

        log.info("연간 회원 나이분포 : " + ageMap);
        return ageMap;
    }

    public Long countUserForPreYear(LocalDate localDate) {
        log.info("작년 회원 통계");
        QUser qUser = QUser.user;

        Integer year = localDate.getYear() - 1;

        List<Long> result = queryFactory
                .select(
                        qUser.createdAt.count()
                )
                .from(qUser)
                .where(
                        qUser.createdAt.year().eq(year)
                        )
                .fetch();

        log.info("작년 회원 통계 : " + result.toString());
        return result.get(0);
    }
}

package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.auth.repository.QUserRepository;
import com.example.finalproject.domain.car.entity.Car;
import com.example.finalproject.domain.car.service.CarService;
import com.example.finalproject.domain.purchases.repository.QPurchasesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final QPurchasesRepository qPurchasesRepository;
    private final QUserRepository qUserRepository;
    private final CarService carService;

    /* 특정 기간(2020~2023)의 연별 신청, 승인, 거절 건수 */
    public Map<String, Object> yearsStat(String startEndYear) {
        String[] startEndYears = startEndYear.split("-");
        String startYear = startEndYears[0];
        String endYear = startEndYears[1];

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customYearsStat(startYear, endYear, null));
        carService.getCarList()
                .stream()
                .map(Car::getType)
                .forEach((type) -> resultMap.put(type, customYearsStat(startYear, endYear, type)));

        return resultMap;
    }

    public Map<String, Object> customYearsStat(String startYear, String endYear, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("purchase", qPurchasesRepository.countPurchaseForYears(startYear, endYear, null, type));
        resultMap.put("approve", qPurchasesRepository.countPurchaseForYears(startYear, endYear, true, type));
        resultMap.put("cancel", qPurchasesRepository.countPurchaseForYears(startYear, endYear, false, type));
        // gender, age, color
        resultMap.put("gender", qPurchasesRepository.countPurchaseByGenderForYears(startYear, endYear, type));
        resultMap.put("age", qPurchasesRepository.countPurchaseByAgeForYears(startYear, endYear, type));
//        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForYears(startYear, endYear, type));

        return resultMap;
    }

    /* 1년간 매달의 신청, 승인, 거절 건수 */
    public Map<String, Object> yearStat(String year) {

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customYearStat(year, null));
        carService.getCarList()
                .stream()
                .map(Car::getType)
                .forEach((type) -> resultMap.put(type, customYearStat(year, type)));

        return resultMap;
    }

    public Map<String, Object> customYearStat(String year, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("purchase", qPurchasesRepository.countPurchaseForYear(year, null, type));
        resultMap.put("approve", qPurchasesRepository.countPurchaseForYear(year, true, type));
        resultMap.put("cancel", qPurchasesRepository.countPurchaseForYear(year, false, type));
        // gender, age, color
        resultMap.put("gender", qPurchasesRepository.countPurchaseByGenderForYear(year, type));
        resultMap.put("age", qPurchasesRepository.countPurchaseByAgeForYear(year, type));
//        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForYear(year, type));
        // pre
        resultMap.put("prePurchase", qPurchasesRepository.countPurchaseForPreYear(year, null, type));
        resultMap.put("preApprove", qPurchasesRepository.countPurchaseForPreYear(year, true, type));
        resultMap.put("preCancel", qPurchasesRepository.countPurchaseForPreYear(year, false, type));

        return resultMap;
    }

    /* 1달간 매주의 신청, 승인 거절 건수 */
    public Map<String, Object> monthStat(String yearMonth) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customMonthStat(yearMonth, null));
        carService.getCarList()
                .stream()
                .map(Car::getType)
                .forEach((type) -> resultMap.put(type, customMonthStat(yearMonth, type)));

        return resultMap;
    }

    public Map<String, Object> customMonthStat(String yearMonth, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("purchase", qPurchasesRepository.countPurchaseForMonth(yearMonth, null, type));
        resultMap.put("approve", qPurchasesRepository.countPurchaseForMonth(yearMonth, true, type));
        resultMap.put("cancel", qPurchasesRepository.countPurchaseForMonth(yearMonth, false, type));
        // gender, age, color
        resultMap.put("gender", qPurchasesRepository.countPurchaseByGenderForMonth(yearMonth, type));
        resultMap.put("age", qPurchasesRepository.countPurchaseByAgeForMonth(yearMonth, type));
//        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForMonth(yearMonth, type));
        // pre
        resultMap.put("prePurchase", qPurchasesRepository.countPurchaseForPreMonth(yearMonth, null, type));
        resultMap.put("preApprove", qPurchasesRepository.countPurchaseForPreMonth(yearMonth, true, type));
        resultMap.put("preCancel", qPurchasesRepository.countPurchaseForPreMonth(yearMonth, false, type));

        return resultMap;
    }

    /* 1주간 매일의 신청, 승인 거절 건수 */
    public Map<String, Object> weekStat(String cal) {
        LocalDate localDate = convertStringToLocalDate(cal);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customWeekStat(localDate, null));
        carService.getCarList()
                .stream()
                .map(Car::getType)
                .forEach((type) -> resultMap.put(type, customWeekStat(localDate, type)));

        return resultMap;
    }

    public Map<String, Object> customWeekStat(LocalDate localDate, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("purchase", qPurchasesRepository.countPurchaseForWeek(localDate, null, type));
        resultMap.put("approve", qPurchasesRepository.countPurchaseForWeek(localDate, true, type));
        resultMap.put("cancel", qPurchasesRepository.countPurchaseForWeek(localDate, false, type));
        // gender, age, color
        resultMap.put("gender", qPurchasesRepository.countPurchaseByGenderForWeek(localDate, type));
        resultMap.put("age", qPurchasesRepository.countPurchaseByAgeForWeek(localDate, type));
//        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForWeek(localDate, type));
        // pre
        resultMap.put("prePurchase", qPurchasesRepository.countPurchaseForPreWeek(localDate, null, type));
        resultMap.put("preApprove", qPurchasesRepository.countPurchaseForPreWeek(localDate, true, type));
        resultMap.put("preCancel", qPurchasesRepository.countPurchaseForPreWeek(localDate, false, type));

        return resultMap;
    }

    // string to localDate
    private LocalDate convertStringToLocalDate(String cal) {
        // 원하는 날짜 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 문자열을 LocalDate로 변환
        return LocalDate.parse(cal, formatter);
    }

    // 연간 회원 통계
    public Map<String, Object> userStat(String year) {

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customUserStat(year));

        return resultMap;
    }

    public Map<String, Object> customUserStat(String year) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("users", qUserRepository.countUserForYear(year));
        // gender, age
        resultMap.put("gender", qUserRepository.countUserByGenderForYear(year));
        resultMap.put("age", qUserRepository.countUserByAgeForYear(year));
        // pre
        resultMap.put("preUser", qUserRepository.countUserForPreYear(year));

        return resultMap;
    }
}

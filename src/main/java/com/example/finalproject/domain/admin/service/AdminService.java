package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.auth.repository.QUserRepository;
import com.example.finalproject.domain.car.entity.Car;
import com.example.finalproject.domain.car.service.CarService;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
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
    public Map<String, Object> yearsStat(String startCal, String endCal) {
        LocalDate startDate = convertStringToLocalDate(startCal);
        LocalDate endDate = convertStringToLocalDate(endCal);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customYearsStat(startDate, endDate, null));
        carService.getCarList()
                .stream()
                .map(Car::getType)
                .forEach((type) -> resultMap.put(type, customYearsStat(startDate, endDate, type)));

        return resultMap;
    }

    public Map<String, Object> customYearsStat(LocalDate strartDate, LocalDate endDate, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("purchase", qPurchasesRepository.countPurchaseForYears(strartDate, endDate, null, type));
        resultMap.put("approve", qPurchasesRepository.countPurchaseForYears(strartDate, endDate, true, type));
        resultMap.put("cancel", qPurchasesRepository.countPurchaseForYears(strartDate, endDate, false, type));
        // gender, age, color
        resultMap.put("gender", qPurchasesRepository.countPurchaseByGenderForYears(strartDate, endDate, type));
        resultMap.put("age", qPurchasesRepository.countPurchaseByAgeForYears(strartDate, endDate, type));
        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForYears(strartDate, endDate, type));

        return resultMap;
    }

    /* 1년간 매달의 신청, 승인, 거절 건수 */
    public Map<String, Object> yearStat(String cal) {
        LocalDate localDate = convertStringToLocalDate(cal);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customYearStat(localDate, null));
        carService.getCarList()
                .stream()
                .map(Car::getType)
                .forEach((type) -> resultMap.put(type, customYearStat(localDate, type)));

        return resultMap;
    }

    public Map<String, Object> customYearStat(LocalDate localDate, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("purchase", qPurchasesRepository.countPurchaseForYear(localDate, null, type));
        resultMap.put("approve", qPurchasesRepository.countPurchaseForYear(localDate, true, type));
        resultMap.put("cancel", qPurchasesRepository.countPurchaseForYear(localDate, false, type));
        // gender, age, color
        resultMap.put("gender", qPurchasesRepository.countPurchaseByGenderForYear(localDate, type));
        resultMap.put("age", qPurchasesRepository.countPurchaseByAgeForYear(localDate, type));
        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForYear(localDate, type));
        // pre
        resultMap.put("prePurchase", qPurchasesRepository.countPurchaseForPreYear(localDate, null, type));
        resultMap.put("preApprove", qPurchasesRepository.countPurchaseForPreYear(localDate, true, type));
        resultMap.put("preCancel", qPurchasesRepository.countPurchaseForPreYear(localDate, false, type));

        return resultMap;
    }

    /* 1달간 매주의 신청, 승인 거절 건수 */
    public Map<String, Object> monthStat(String cal) {
        LocalDate localDate = convertStringToLocalDate(cal);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customMonthStat(localDate, null));
        carService.getCarList()
                .stream()
                .map(Car::getType)
                .forEach((type) -> resultMap.put(type, customMonthStat(localDate, type)));

        return resultMap;
    }

    public Map<String, Object> customMonthStat(LocalDate localDate, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("purchase", qPurchasesRepository.countPurchaseForMonth(localDate, null, type));
        resultMap.put("approve", qPurchasesRepository.countPurchaseForMonth(localDate, true, type));
        resultMap.put("cancel", qPurchasesRepository.countPurchaseForMonth(localDate, false, type));
        // gender, age, color
        resultMap.put("gender", qPurchasesRepository.countPurchaseByGenderForMonth(localDate, type));
        resultMap.put("age", qPurchasesRepository.countPurchaseByAgeForMonth(localDate, type));
        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForMonth(localDate, type));
        // pre
        resultMap.put("prePurchase", qPurchasesRepository.countPurchaseForPreMonth(localDate, null, type));
        resultMap.put("preApprove", qPurchasesRepository.countPurchaseForPreMonth(localDate, true, type));
        resultMap.put("preCancel", qPurchasesRepository.countPurchaseForPreMonth(localDate, false, type));

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
        resultMap.put("color", qPurchasesRepository.countPurchaseByColorForWeek(localDate, type));
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
    public Map<String, Object> userStat(String cal) {
        LocalDate localDate = convertStringToLocalDate(cal);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", customUserStat(localDate));

        return resultMap;
    }

    public Map<String, Object> customUserStat(LocalDate localDate) {
        Map<String, Object> resultMap = new HashMap<>();
        // current
        resultMap.put("users", qUserRepository.countUserForYear(localDate));
        // gender, age
        resultMap.put("gender", qUserRepository.countUserByGenderForYear(localDate));
        resultMap.put("age", qUserRepository.countUserByAgeForYear(localDate));
        // pre
        resultMap.put("preUser", qUserRepository.countUserForPreYear(localDate));

        return resultMap;
    }
}

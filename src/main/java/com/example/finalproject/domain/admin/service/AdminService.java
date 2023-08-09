package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final PurchasesRepository purchasesRepository;

    //----------------------------------------- getYear--------------------------------------------------//
    public void getAnalysisForYear(String year) {
        long[] purchase = new long[12];
        long[] approve = new long[12];
        long[] cancel = new long[12];
        long[] purchase_model1 = new long[12];
        long[] approve_model1 = new long[12];
        long[] cancel_model1 = new long[12];
        long[] purchase_model2 = new long[12];
        long[] approve_model2 = new long[12];
        long[] cancel_model2 = new long[12];
        String model1 = "K3";
        String model2 = "K5";
        // 연도를 기반으로 시작과 끝 날짜를 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), 1, 1);
        LocalDate endDate = LocalDate.of(Integer.parseInt(year), 12, 31);

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        LocalDate prevStartDate = LocalDate.of(Integer.parseInt(year) - 1, 1, 1);
        LocalDate prevEndDate = LocalDate.of(Integer.parseInt(year) - 1, 12, 31);

        Long prevPurchaseTotal = purchasesRepository.findAnnualCountWithoutApprove(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59));
        Long prevApproveTotal = purchasesRepository.findAnnualCountBetweenDates(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), true);
        Long prevCancelTotal = purchasesRepository.findAnnualCountBetweenDates(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), false);

        Long prevPurchaseModel1 = purchasesRepository.findTypeAnnualCountWithoutApprove(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), model1);
        Long prevApproveModel1 = purchasesRepository.findTypeAnnualCountBetweenDates(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), true, model1);
        Long prevCancelModel1 = purchasesRepository.findTypeAnnualCountBetweenDates(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), false, model1);

        Long prevPurchaseModel2 = purchasesRepository.findTypeAnnualCountWithoutApprove(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), model2);
        Long prevApproveModel2 = purchasesRepository.findTypeAnnualCountBetweenDates(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), true, model2);
        Long prevCancelModel2 = purchasesRepository.findTypeAnnualCountBetweenDates(prevStartDate.atStartOfDay(), prevEndDate.atTime(23, 59, 59), false, model2);

        List<Object[]> results_purchase = purchasesRepository.findMonthlyCountWithoutApprove(startDateTime, endDateTime);
        List<Object[]> results_approve = purchasesRepository.findMonthlyCountBetweenDates(startDateTime, endDateTime, true);
        List<Object[]> results_cancel = purchasesRepository.findMonthlyCountBetweenDates(startDateTime, endDateTime, false);

        List<Object[]> results_purchase_model1 = purchasesRepository.findTypeMonthlyCountWithNullApprove(startDateTime, endDateTime, model1);
        List<Object[]> results_approve_model1 = purchasesRepository.findTypeMonthlyCountBetweenDates(startDateTime, endDateTime, true, model1);
        List<Object[]> results_cancel_model1 = purchasesRepository.findTypeMonthlyCountBetweenDates(startDateTime, endDateTime, false, model1);

        List<Object[]> results_purchase_model2 = purchasesRepository.findTypeMonthlyCountWithNullApprove(startDateTime, endDateTime, model2);
        List<Object[]> results_approve_model2 = purchasesRepository.findTypeMonthlyCountBetweenDates(startDateTime, endDateTime, true, model2);
        List<Object[]> results_cancel_model2 = purchasesRepository.findTypeMonthlyCountBetweenDates(startDateTime, endDateTime, false, model2);
        // 모든 달에 대한 기본 값을 설정 (0으로 초기화)
        processResults(results_purchase_model2, purchase_model2);
        processResults(results_approve_model2, approve_model2);
        processResults(results_cancel_model2, cancel_model2);
        processResults(results_purchase_model1, purchase_model1);
        processResults(results_approve_model1, approve_model1);
        processResults(results_cancel_model1, cancel_model1);
        processResults(results_purchase, purchase);
        processResults(results_approve, approve);
        processResults(results_cancel, cancel);
        // 결과 출력
        for (int i = 0; i < 12; i++) {
            System.out.println("대기Year: " + year + ", Month: " + (i + 1) + ", Count: " + purchase[i]);
        }
        for (int i = 0; i < 12; i++) {
            System.out.println("승인 Year: " + year + ", Month: " + (i + 1) + ", Count: " + approve[i]);
        }
        for (int i = 0; i < 12; i++) {
            System.out.println("거부 Year: " + year + ", Month: " + (i + 1) + ", Count: " + cancel[i]);
        }
        System.out.printf("PreYear Total - Purchase: %d, Approve: %d, Cancel: %d\n", prevPurchaseTotal, prevApproveTotal, prevCancelTotal);
        System.out.printf("PreYear Model1 - Purchase: %d, Approve: %d, Cancel: %d\n", prevPurchaseModel1, prevApproveModel1, prevCancelModel1);
        System.out.printf("PreYear Model2 - Purchase: %d, Approve: %d, Cancel: %d\n", prevPurchaseModel2, prevApproveModel2, prevCancelModel2);
    }

    private void processResults(List<Object[]> results, long[] targetArray) {
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            targetArray[month - 1] = count;
        }
    }

    //----------------------------------------- getMonth--------------------------------------------------//
    public void getAnalysisForMonth(String cal) {
        int year = Integer.parseInt(cal.split("-")[0]);
        int month = Integer.parseInt(cal.split("-")[1]);

        // 해당 월의 시작 및 끝 날짜 설정
        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, month, startDateTime.toLocalDate().lengthOfMonth(), 23, 59, 59);

        // 전월의 시작 및 끝 날짜 설정
//        LocalDateTime prevStartDate = startDate.minusMonths(1);
//        LocalDateTime prevEndDate = prevStartDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

        // This month's data arrays
        long[] totalPurchase = new long[4];
        long[] totalApprove = new long[4];
        long[] totalCancel = new long[4];
        long[] model1Purchase = new long[4];
        long[] model1Approve = new long[4];
        long[] model1Cancel = new long[4];
        long[] model2Purchase = new long[4];
        long[] model2Approve = new long[4];
        long[] model2Cancel = new long[4];

        String model1="K3";
        String model2="K5";
        for (int week = 1; week <= 4; week++) {
            LocalDateTime startOfWeek = getStartOfWeek(startDateTime, week);
            LocalDateTime endOfWeek = getEndOfWeek(startDateTime, week);

            totalPurchase[week-1] = purchasesRepository.findWeeklyCountWithoutApprove(startOfWeek, endOfWeek);
            totalApprove[week-1] = purchasesRepository.findTypeWeeklyCountByApprove(startOfWeek, endOfWeek, true);
            totalCancel[week-1] = purchasesRepository.findTypeWeeklyCountByApprove(startOfWeek, endOfWeek, false);

            model1Purchase[week-1] = purchasesRepository.findTypeWeeklyCountWithoutApprove(startOfWeek, endOfWeek, model1);
            model1Approve[week-1] = purchasesRepository.findTypeWeeklyCountByApprove(startOfWeek, endOfWeek, model1, true);
            model1Cancel[week-1] = purchasesRepository.findTypeWeeklyCountByApprove(startOfWeek, endOfWeek, model1, false);

            model2Purchase[week-1] = purchasesRepository.findTypeWeeklyCountWithoutApprove(startOfWeek, endOfWeek, model2);
            model2Approve[week-1] = purchasesRepository.findTypeWeeklyCountByApprove(startOfWeek, endOfWeek, model2, false);
            model2Cancel[week-1] = purchasesRepository.findTypeWeeklyCountByApprove(startOfWeek, endOfWeek, model2, false);
        }
        for (int i=1;i<=4;i++){
            System.out.println("total Week " + i);
            System.out.println("Total Purchase: " + totalPurchase[i-1]);
            System.out.println("Total Approve: " + totalApprove[i-1]);
            System.out.println("Total Cancel: " + totalCancel[i-1]);
            System.out.println("");
        }
        for (int i=1;i<=4;i++){
            System.out.println("model 1 Week " + i);
            System.out.println("Model1 Purchase: " + model1Purchase[i-1]);
            System.out.println("Model1 Approve: " + model1Approve[i-1]);
            System.out.println("Model1 Cancel: " + model1Cancel[i-1]);
            System.out.println("");
        }

        for (int i=1;i<=4;i++){
            System.out.println("model 2 Week " + i);
            System.out.println("Model2 Purchase: " + model2Purchase[i-1]);
            System.out.println("Model2 Approve: " + model2Approve[i-1]);
            System.out.println("Model2 Cancel: " + model2Cancel[i-1]);
            System.out.println("");
        }
        // Construct the response object
    }
    public LocalDateTime getStartOfWeek(LocalDateTime date, int week) {
        return date.with(TemporalAdjusters.firstDayOfMonth()).plusDays(7 * (week - 1));
    }

    public LocalDateTime getEndOfWeek(LocalDateTime date, int week) {
        LocalDateTime endOfWeek = date.with(TemporalAdjusters.firstDayOfMonth()).plusDays(7 * week - 1);
        if (endOfWeek.getMonth() != date.getMonth()) {
            return date.with(TemporalAdjusters.lastDayOfMonth());
        }
        return endOfWeek;
    }

}

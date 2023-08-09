package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final PurchasesRepository purchasesRepository;
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
        String model1="K3";
        String model2="K5";
        // 연도를 기반으로 시작과 끝 날짜를 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), 1, 1);
        LocalDate endDate = LocalDate.of(Integer.parseInt(year), 12, 31);

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

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
            System.out.println("대기Year: " + year + ", Month: " + (i + 1) + ", Count: " + purchase_model2[i]);
        }
        for (int i = 0; i < 12; i++) {
            System.out.println("승인 Year: " + year + ", Month: " + (i + 1) + ", Count: " + approve_model2[i]);
        }
        for (int i = 0; i < 12; i++) {
            System.out.println("거부 Year: " + year + ", Month: " + (i + 1) + ", Count: " + cancel_model2[i]);
        }

    }
    private void processResults(List<Object[]> results, long[] targetArray) {
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            targetArray[month - 1] = count;
        }
    }
}

package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final PurchasesRepository purchasesRepository;
    public void getAnalysisForYear(String year) {
        // 연도를 기반으로 시작과 끝 날짜를 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), 1, 1);
        LocalDate endDate = LocalDate.of(Integer.parseInt(year), 12, 31);

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> results_purchase;
        results_purchase = purchasesRepository.findMonthlyCountWithoutApprove(startDateTime, endDateTime);
        List<Object[]> results_approve;
        results_approve = purchasesRepository.findMonthlyCountBetweenDates(startDateTime, endDateTime, true);
        List<Object[]> results_cancel;
        results_cancel = purchasesRepository.findMonthlyCountBetweenDates(startDateTime, endDateTime, false);


        // 모든 달에 대한 기본 값을 설정 (0으로 초기화)
        long[] purchase = new long[12];
        long[] approve = new long[12];
        long[] cancel = new long[12];

        // 데이터베이스 결과로 배열 값 업데이트
        for (Object[] result : results_purchase) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            purchase[month - 1] = count;
        }
        for (Object[] result : results_approve) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            approve[month - 1] = count;
        }
        for (Object[] result : results_cancel) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            cancel[month - 1] = count;
        }
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
    }
}

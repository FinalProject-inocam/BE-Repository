package com.example.finalproject.domain.admin.controller;

import com.example.finalproject.domain.admin.service.AdminService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/api/stat/purchases/chart")
    public ApiResponse<?> getStat(@RequestParam(name = "cal") String cal,
                                  @RequestParam(name = "term") String period) {

        switch (period) {
            case "getYear":
                //년별 분석
                adminService.getAnalysisForYear(cal);
                break;
            case "getMonth":
                // 월별 분석
//                adminService.getAnalysisForMonth(cal);
                break;
            case "getWeek":
                // 주별 분석
//                adminService.getAnalysisForWeek(cal);
                break;
        }
        return null;
    }

//    @GetMapping("/api/auth/stat")
//    public ApiResponse<?> getStatTest(@RequestParam(name = "year") int year,
//                                      @RequestParam(name = "type") String type) {
//        List<Map<Long, Long>> statList = adminService.statTest(year, type);
//        return ResponseUtils.ok(statList);
//    }
}

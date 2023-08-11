package com.example.finalproject.domain.admin.controller;

import com.example.finalproject.domain.admin.service.AdminService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/api/auth/stat/purchases/chart")
    public ApiResponse<?> getStat(@RequestParam(name = "cal") String cal,
                                  @RequestParam(name = "term") String period) {

        switch (period) {
            case "getYear":
                //년별 분석
                adminService.getAnalysisForYear(cal);
                break;
            case "getMonth":
                // 월별 분석
                adminService.getAnalysisForMonth(cal);
                break;
            case "getWeek":
                // 주별 분석
//                adminService.getAnalysisForWeek(cal);
                break;
        }
        return null;
    }
    /*---------------------------------------------------------------------------------------------------*/

    @GetMapping("/api/auth/stat")
    public ApiResponse<?> getStatTest(@RequestParam String cal) {
        Map<String, Object> yearMap = adminService.yearStat(cal);
        return ResponseUtils.ok(yearMap);
    }

    @GetMapping("/api/auth/stat2")
    public ApiResponse<?> getStatTest2(@RequestParam String cal) {
        Map<String, Object> statList = adminService.monthStat(cal);
        return ResponseUtils.ok(statList);
    }

    @GetMapping("/api/auth/stat3")
    public ApiResponse<?> getStatTest3(@RequestParam String cal) {
        Map<String, Object> statList = adminService.weekStat(cal);
        return ResponseUtils.ok(statList);
    }
}

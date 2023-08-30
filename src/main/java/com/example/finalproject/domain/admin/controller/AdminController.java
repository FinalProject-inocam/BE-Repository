package com.example.finalproject.domain.admin.controller;

import com.example.finalproject.domain.admin.dto.ReleaseDecidereqDto;
import com.example.finalproject.domain.admin.service.AdminListService;
import com.example.finalproject.domain.admin.service.AdminService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final AdminListService adminListService;


    @GetMapping("/totalList")
    public ApiResponse<?> totalList() {
        return ResponseUtils.ok(adminListService.totalList());
    }

    @GetMapping("/allList")
    public ApiResponse<?> allList(@RequestParam int page,
                                  @RequestParam int size) {
        return ResponseUtils.ok(adminListService.allList(page, size));
    }

    @GetMapping("/purchases/{purchaseId}")
    public ApiResponse<?> getone(@PathVariable(name = "purchaseId") Long purchaseId) {
        return ResponseUtils.ok(adminListService.getone(purchaseId));
    }

    @PatchMapping("/purchases/{purchaseId}")
    public ApiResponse<?> releaseDecide(@PathVariable(name = "purchaseId") Long purchaseId,
                                        @RequestBody ReleaseDecidereqDto releaseDecidereqDto) {
        return ResponseUtils.ok(adminListService.releaseDecide(purchaseId, releaseDecidereqDto));
    }
    /*---------------------------------------------------------------------------------------------------*/

    @GetMapping("/stats/purchases/years")
    public ApiResponse<?> getStatYears(@RequestParam String startCal,
                                       @RequestParam String endCal) {
        Map<String, Object> statMap = adminService.yearsStat(startCal, endCal);
        return ResponseUtils.ok(statMap);
    }

    @GetMapping("/stats/purchases/year")
    public ApiResponse<?> getStatYear(@RequestParam String year) {
        Map<String, Object> statMap = adminService.yearStat(year);
        return ResponseUtils.ok(statMap);
    }

    @GetMapping("/stats/purchases/month")
    public ApiResponse<?> getStatMonth(@RequestParam String yearMonth) {
        Map<String, Object> statMap = adminService.monthStat(yearMonth);
        return ResponseUtils.ok(statMap);
    }

    @GetMapping("/stats/purchases/week")
    public ApiResponse<?> getStatWeek(@RequestParam String cal) {
        Map<String, Object> statMap = adminService.weekStat(cal);
        return ResponseUtils.ok(statMap);
    }

    /*----------------------------------------------------------------------------*/
    @GetMapping("/stats/users/year")
    public ApiResponse<?> getUserYear(@RequestParam String cal) {
        Map<String, Object> statMap = adminService.userStat(cal);
        return ResponseUtils.ok(statMap);
    }
}

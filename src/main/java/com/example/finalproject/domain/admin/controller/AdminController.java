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
public class AdminController {
    private final AdminService adminService;
    private final AdminListService adminListService;

    @GetMapping("/api/stat/purchases/chart")
    public ApiResponse<?> getStat(@RequestParam(name="cal") String cal,
                                  @RequestParam(name="term")String period){

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
                adminService.getAnalysisForWeek(cal);
                break;
        }
        return null;
    }
    @GetMapping("/api/admin/totalList")
    public ApiResponse<?> totalList(){
        return ResponseUtils.ok(adminListService.totalList());
    }
    @GetMapping("/api/admin/approveList")
    public ApiResponse<?> approveList(@RequestParam("page") int page,
                                      @RequestParam("size") int size){
        return ResponseUtils.ok(adminListService.approveList(page,size));
    }
    @GetMapping("/api/admin/decideList")
    public ApiResponse<?> decideList(@RequestParam("page") int page,
                                      @RequestParam("size") int size){
        return ResponseUtils.ok(adminListService.decideList(page,size));
    }
    @GetMapping("/api/admin/denyList")
    public ApiResponse<?> denyList(@RequestParam("page") int page,
                                      @RequestParam("size") int size){
        return ResponseUtils.ok(adminListService.denyList(page,size));
    }

    @PatchMapping("/api/admin/purchases/{purchaseId}")
    public ApiResponse<?> releaseDecide(@PathVariable(name = "purchaseId") Long purchaseId,
                                        @RequestBody ReleaseDecidereqDto releaseDecidereqDto){
        return ResponseUtils.ok(adminListService.releaseDecide(purchaseId,releaseDecidereqDto));
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

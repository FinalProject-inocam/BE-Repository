package com.example.finalproject.domain.mail.controller;


import com.example.finalproject.domain.mail.service.MailService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MailController {
    private final MailService mailService;

    @GetMapping("/verify")
    public ApiResponse<?> execMail(@RequestParam(name = "email") String email) {
        SuccessCode successCode = mailService.send(email);
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/checkcode")
    public ApiResponse<?> checkCode(@RequestParam(name = "email") String email,
                                    @RequestParam(name = "code") String code) {
        SuccessCode successCode = mailService.checkCode(email, code);
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/findPass")
    public ApiResponse<?> findCode(@RequestParam(name = "email") String email) {
        return ResponseUtils.ok(mailService.findPass(email));
    }
}
package com.example.finalproject.domain.mail.controller;



import com.example.finalproject.domain.mail.service.MailService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/email")
    public ApiResponse<?> execMail(@RequestParam(name="email") String email) {
        SuccessCode successCode=mailService.send(email);
        return ResponseUtils.ok(successCode);
    }
}
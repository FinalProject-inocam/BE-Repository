package com.example.finalproject.domain.mail.service;

import com.example.finalproject.auth.repository.UserRepository;
import com.example.finalproject.domain.mail.dto.MailDto;
import com.example.finalproject.global.enums.SuccessCode;
import com.sun.mail.util.logging.MailHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    String code = randomcode();
    private static final String FROM_ADDRESS = "inomotorservice@gmail.com";
    public SuccessCode send(String to) {
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress("inomotorservice@gmail.com", "이노모터서비스"));
            //메일 제목 설정
            helper.setSubject("[이노모터스] 인증코드입니다");

            //수신자 설정
            helper.setTo(to);

            //템플릿에 전달할 데이터 설정
            Context context = new Context();
            context.setVariable("name", code);

            //메일 내용 설정 : 템플릿 프로세스
            String html = templateEngine.process("welcome", context);
            helper.setText(html, true);

            //메일 보내기
            javaMailSender.send(mimeMessage);
            return SuccessCode.MAIL_SUCCESS;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return SuccessCode.LIKE_SUCCESS;
    }
    public String randomcode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }
    private String setContext(String code, String templateName) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(templateName, context);
    }
}
package com.example.finalproject.domain.mail.service;


import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.mail.entity.AuthCode;
import com.example.finalproject.domain.mail.repository.AuthCodeRepository;
import com.example.finalproject.global.enums.SuccessCode;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final AuthCodeRepository authCodeRepository;
    private final UserRepository userRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    String code = randomcode();
    private static final String FROM_ADDRESS = "inomotorservice@gmail.com";
    public SuccessCode send(String to) {
        if (checkEmail(to)) {
            throw new IllegalArgumentException("중복되는 이메일이 있습니다.");
        }
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
            // 인증코드,이메일 저장
            AuthCode authCode=new AuthCode(to,code);
            authCodeRepository.save(authCode);
            return SuccessCode.MAIL_SUCCESS;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("메일 전송 실패");
        }
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
    public Boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
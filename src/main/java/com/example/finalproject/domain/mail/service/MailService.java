package com.example.finalproject.domain.mail.service;


import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.mail.dto.MailDto;
import com.example.finalproject.domain.mail.exception.MailNotFoundException;
import com.example.finalproject.domain.mail.repository.AuthCodeRepository;
import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.utils.RedisUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional
@EnableScheduling
public class MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final AuthCodeRepository authCodeRepository;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String FROM_ADDRESS = "inomotorservice@gmail.com";

    public SuccessCode send(String to) {
        if (checkEmail(to)) {
            throw new MailNotFoundException(ErrorCode.DUPLICATE_EMAIL);
        }
        try {
            String code = randomcode();
            redisUtil.setDataExpire(code, to, 60 * 5L);
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
            // 인증 코드의 만료 시간 설정 (예: 30분 후)
            return SuccessCode.MAIL_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MailNotFoundException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public SuccessCode checkCode(MailDto mailDto) {
        log.info(mailDto.getCode());
        String key = redisUtil.getData(mailDto.getCode());
        if (key == null) {
            throw new MailNotFoundException(ErrorCode.INVALID_CODE);
        }
        log.info(key);
        if (key.equals(mailDto.getEmail())) {
            return SuccessCode.VERIFY_COMPLETE;
        } else {
            throw new MailNotFoundException(ErrorCode.INVALID_CODE);
        }
    }

    public String randomcode() {
        StringBuilder code = new StringBuilder(6);
        int charactersLength = CHARACTERS.length();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * charactersLength);
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
package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.admin.dto.AllPurchasseResponseDto;
import com.example.finalproject.domain.admin.dto.ReleaseDecidereqDto;
import com.example.finalproject.domain.admin.dto.TotalListResponseDto;
import com.example.finalproject.domain.admin.exception.AdminNotFoundException;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.mail.exception.MailNotFoundException;
import com.example.finalproject.domain.purchases.dto.response.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.entity.Purchase;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.PageResponse;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminListService {
    private final PurchasesRepository purchasesRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "helper@innomotors.shop";

    // 페이지 나누기
    @Transactional
    public Page<PurchasesResponseDto> allList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "purchaseId"));
        Page<Purchase> purchaseList = purchasesRepository.findAll(pageable);
        List<AllPurchasseResponseDto> purchasesResponseDtoList = new ArrayList<>();
        for (Purchase purchase : purchaseList) {
            AllPurchasseResponseDto allPurchasseResponseDto = new AllPurchasseResponseDto(purchase);
            purchasesResponseDtoList.add(allPurchasseResponseDto);
        }
        return new PageResponse(purchasesResponseDtoList, pageable, purchaseList.getTotalElements());
    }


    public TotalListResponseDto totalList() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 오늘 날짜의 시작 및 끝 날짜 설정
        LocalDateTime startDateTime = LocalDateTime.of(currentDateTime.toLocalDate(), LocalTime.MIDNIGHT); // 오늘 날짜의 시작 시간
        LocalDateTime endDateTime = LocalDateTime.of(currentDateTime.toLocalDate(), LocalTime.MAX); // 오늘 날짜의 끝 시간

//        Long needApprovalCount = purchasesRepository.countByApprove(null);
        Long needApprovalCount = purchasesRepository.countByApproveIsNullAndCreatedAtBetween(startDateTime, endDateTime);
        Long approvedCount = purchasesRepository.countByApproveIsTrueAndCreatedAtBetween(startDateTime, endDateTime);
        Long deniedCount = purchasesRepository.countByApproveIsFalseAndCreatedAtBetween(startDateTime, endDateTime);

        System.out.println("출고 대기 : " + needApprovalCount);
        System.out.println("출고 승인 : " + approvedCount);
        System.out.println("출고 거절 : " + deniedCount);
        return new TotalListResponseDto(needApprovalCount, approvedCount, deniedCount);
    }

    @Transactional
    public SuccessCode releaseDecide(Long id, ReleaseDecidereqDto releaseDecidereqDto) {
        log.info("접근");
        Purchase purchase = purchasesRepository.findById(id).orElseThrow(
                () -> new AdminNotFoundException(NOT_FOUND_DATA)
        );
        log.info("출고인 닉네임 : "+purchase.getUser().getNickname());
        if (releaseDecidereqDto.getApprove()) {
            if (releaseDecidereqDto.getDeliveryDate() == null) {
                throw new NullPointerException("배송 날짜가 없습니다");
            }
            purchase.update(releaseDecidereqDto.getApprove(), releaseDecidereqDto.getDeliveryDate());
            if(purchase.getAlarm()){
                send(purchase);
            }
            log.info("승인 알람 : "+purchase.getAlarm());
            log.info("배달일자 : "+purchase.getDeliveryDate());
            return SuccessCode.PURCHASE_APPROVE;
        } else {
            if (releaseDecidereqDto.getDenyMessage() == null || releaseDecidereqDto.getDenyMessage().trim().isEmpty()||releaseDecidereqDto.getDenyMessage().isBlank()) {
                throw  new NullPointerException("반려사유가 없습니다");
            }
            purchase.update(releaseDecidereqDto.getApprove(), releaseDecidereqDto.getDenyMessage());
            if(purchase.getAlarm()){
                send(purchase);
            }
            log.info("승인 알람 : "+purchase.getAlarm());
            log.info("반려 사유 : "+purchase.getDenyMessage());
            return SuccessCode.PURCHASE_DENIED;
        }
    }

    public PurchasesResponseDto getone(Long purchaseId) {
        Purchase purchase = purchasesRepository.findById(purchaseId).orElseThrow(() ->
                new NullPointerException("존재하지않는 신청입니다.")
        );
        PurchasesResponseDto purchasesResponseDto = new PurchasesResponseDto(purchase);
        return purchasesResponseDto;
    }

    @Transactional
    public void send(Purchase purchase) {

        try {
            log.info("출고인 이메일 : "+purchase.getUser().getEmail());
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress("helper@innomotors.shop ", "이노모터서비스"));
            //메일 제목 설정
            helper.setSubject("[이노모터스] 차량출고 신청");
            //수신자 설정
            helper.setTo(purchase.getUser().getEmail());
            //템플릿에 전달할 데이터 설정
            Context context = new Context();

            if(purchase.getApprove()) {
                helper.setSubject("[이노모터스] 차량출고 승인 알림");
                Date deliveryDate = purchase.getDeliveryDate();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String date = formatter.format(deliveryDate);
                context.setVariable("name", date);
                String html = templateEngine.process("approveMail", context);
                helper.setText(html, true);
                //메일 보내기
                javaMailSender.send(mimeMessage);
            }
            else{
                helper.setSubject("[이노모터스] 차량출고 반려 알림");
                context.setVariable("name", purchase.getDenyMessage());
                String html = templateEngine.process("denyMail", context);
                helper.setText(html, true);
                //메일 보내기
                javaMailSender.send(mimeMessage);
            }
            log.info("이메일 발송 완료 ");

        } catch (Exception e) {
            e.printStackTrace();
            throw new MailNotFoundException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

}

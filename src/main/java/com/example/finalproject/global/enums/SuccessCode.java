package com.example.finalproject.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NO_CONTENT;
@Getter
public enum SuccessCode {
    /* 200 OK : 요청이 성공적으로 완료되었다는 의미입니다. */
    USER_LOGIN_SUCCESS(OK, "로그인 성공"),
    USER_LOGOUT_SUCCESS(OK, "로그아웃 성공"),
    POST_UPDATE_SUCCESS(OK, "게시글 수정 성공"),
    POST_DELETE_SUCCESS(OK, "게시글 삭제 성공"),
    COMMENT_UPDATE_SUCCESS(OK, "댓글 수정 완료"),
    COMMENT_DELETE_SUCCESS(OK, "댓글 삭제 성공"),
    PURCHASES_DELETE_SUCCESS(OK, "신청 취소 성공"),
    PURCHASES_UPDATE_SUCCESS(OK, "신청 수정 성공"),
    MYPAGE_UPDATE_SUCCESS(OK, "마이페이지 수정"),
    VERIFY_COMPLETE(OK,"인증이 완료 되었습니다"),
    PURCHASE_APPROVE(OK,"출고 승인이 완료 되었습니다"),
    PURCHASE_DENIED(OK,"출고 승인이 거절 되었습니다"),
    REVISIT_TRUE(OK,"재방문의사 있음"),
    REVISIT_FALSE(OK,"재방문의사 없음"),

    LIKE_CANCEL(OK, "좋아요 취소"),
    DISLIKE_CANCEL(OK, "싫어요 취소"),
    MAIL_SUCCESS(OK, "메일 발송 완료"),
    USER_CHECK_EMAIL_TRUE(OK, "사용가능한 이메일입니다."),
    USER_CHECK_EMAIL_FALSE(OK, "이미 존재하는 이메일입니다."),
    USER_CHECK_NICKNAME_TRUE(OK, "사용 가능한 닉네임입니다."),
    USER_CHECK_NICKNAME_FALSE(OK, "이미 존재하는 닉네임입니다."),
    /* 201 CREATED : 요청이 성공적이었으며 그 결과로 새로운 리소스가 생성 되었다는 의미입니다. */
    USER_SIGNUP_SUCCESS(CREATED, "회원가입 성공"),
    POST_CREATE_SUCCESS(CREATED, "게시글 작성 성공"),
    COMMENT_CREATE_SUCCESS(CREATED, "댓글 작성 완료"),
    PURCHASES_CREATE_SUCCESS(CREATED, "출고 신청 완료"),
    LIKE_SUCCESS(CREATED, "좋아요 성공"),
    DISLIKE_SUCCESS(CREATED, "싫어요 성공"),
    /* 204 NO CONTENT : 요청이 성공적이였으나, 그 결과로 출력해줄 값이 없다는 의미입니다. */
    NO_SHOP_SUCCESS(NO_CONTENT, "조회 가능한 가게가 없습니다.");

    private final HttpStatus httpStatus;
    private final String detail;

    SuccessCode(HttpStatus httpStatus, String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}

package com.example.finalproject.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    /* 400 BAD_REQUEST : 이 응답은 잘못된 문법으로 인해 서버가 요청을 이해할 수 없다는 의미입니다. */
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    PURCHASES_DELETE_FAIL(BAD_REQUEST, "신청 취소 실패"),
    DUPLICATE_EMAIL(BAD_REQUEST, "이미 등록된 email입니다"),
    INVALID_CODE(BAD_REQUEST, "유효하지 않은 code입니다"),
    DIFFIRENT_USER(BAD_REQUEST, "후기작성자가 아닙니다"),
    LIMIT_MAX_IMAGE(BAD_REQUEST, "최대 4개까지 업로드 가능합니다"),
    NOT_FOUND_KEYWORD(BAD_REQUEST, "검색어를 입력하세요"),
    EMAIL_SEND_FAIL(BAD_REQUEST, "이메일 발송 실패"),


    /* 401 UNAUTHORIZED : 인증되지 않았다는 의미입니다. */
    INVALID_ADMIN(UNAUTHORIZED, "관리자 권한이 없습니다."),
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_USER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),

    /* 403 FORBIDDEN : 클라이언트가 콘텐츠에 접근할 권리를 가지고 있지 않다는 의미입니다.*/
    USER_LOGIN_NEED(FORBIDDEN, "로그인이 필요합니다."),
    USER_LOGIN_FAIL(FORBIDDEN, "이메일 혹은 비밀번호가 틀렸습니다."),
    NO_AUTHORITY_TO_DATA(FORBIDDEN, "해당 게시물에 대한 권한이 없습니다."),
    MORE_AGREEMENT_NEEDED(FORBIDDEN, "모든 권한을 허용해 주세요"),

    /* 404 NOT_FOUND : 서버는 요청 받은 리소스를 찾을 수 없다는 의미입니다. */
    USER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    NOT_FOUND_CLIENT(NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_DATA(NOT_FOUND, "해당 게시물을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),
    SHOP_NOT_FOUND(NOT_FOUND, "해당 가게를 찾을 수 없습니다."),
    NOT_FOUND_EMAIL(NOT_FOUND,"등록되지않은 이메일입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다");

    private final HttpStatus httpStatus;
    private final String detail;

    ErrorCode(HttpStatus httpStatus, String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}

//package com.example.finalproject.domain.auth.controller;
//import com.example.finalproject.domain.auth.dto.SignupRequestDto;
//import com.example.finalproject.domain.auth.service.UserService;
//import com.example.finalproject.global.enums.SuccessCode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private UserService userService;
//
//    private SignupRequestDto signupRequestDto;
//
//    @Nested
//    @DisplayName("유저 컨트롤러 성공 케이스")
//    class ControllerSuccessCase {
//        @BeforeEach
//        private void init1() {
//            signupRequestDto = SignupRequestDto.builder()
//                    .email("tttt@test.com")
//                    .password("pass11!!")
//                    .nickname("")
//                    .gender(null)
//                    .birthYear(null)
//                    .phoneNumber("123456789")
//                    .admin(false)
//                    .build();
//        }
//
//        @Test
//        @DisplayName("[POST] 회원가입 성공")
//        void signup() throws Exception {
//            // given
//            when(userService.signup(any()))
//                    .thenReturn(SuccessCode.USER_SIGNUP_SUCCESS);
//
//            // when-then
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(signupRequestDto)))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("msg").exists())
//                    .andExpect(jsonPath("msg").value("회원가입 성공"))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("[GET] 이메일 중복 확인")
//        void checkEmail() throws Exception {
//            // Given
//            String checkEmail = "minj33i3@test.com";
//            when(userService.checkEmail(checkEmail))
//                    .thenReturn(SuccessCode.USER_CHECK_EMAIL_TRUE);
//
//            // When-Then
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/email")
//                            .param("email", checkEmail)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("msg").value("사용가능한 이메일입니다."))
//                    .andDo(print());
//        }
//
//
//        @Test
//        @DisplayName("[GET] 닉네임 중복 확인")
//        void checkNickname() throws Exception {
//            // Given
//            String checkNickname = "샌드위치";
//            when(userService.checkNickname(checkNickname))
//                    .thenReturn(SuccessCode.USER_CHECK_NICKNAME_TRUE);
//
//            // When-Then
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/nickname")
//                            .param("nickname", checkNickname)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("msg").value("사용 가능한 닉네임입니다."))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("[GET] 로그아웃 성공 테스트")
//        void logout() throws Exception {
//            // given
//            when(userService.logout(any()))
//                    .thenReturn(SuccessCode.USER_LOGOUT_SUCCESS);
//
//            // when-then
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/logout")
//                            .contentType(MediaType.APPLICATION_JSON))   // json 형식으로 데이터 보내기
//                    .andExpect(status().isOk()) // 상태코드
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("msg").exists())
//                    .andExpect(jsonPath("msg").value("로그아웃 성공"))
//                    .andDo(print());
//        }
//    }
//
//    @Nested
//    @DisplayName("유저 컨트롤러 실패 케이스")
//    class ControllerFailCase {
//        @BeforeEach
//        private void init2() {
//            signupRequestDto = SignupRequestDto.builder()
//                    .email("tttt@com.com")
//                    .password("4564lkl77!")
//                    .nickname(null)
//                    .gender(null)
//                    .birthYear(null)
//                    .phoneNumber("123456789")
//                    .admin(false)
//                    .build();
//        }
//
//        @Test
//        @DisplayName("[POST] 회원가입 실패 - 이메일 형식 안맞음")
//        void 이메일_형식_아님() throws Exception {
////                when(userService.signup(any()))
////                        .thenReturn(SuccessCode.USER_SIGNUP_SUCCESS);
//
//            // When-Then
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(signupRequestDto)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("msg").exists())
//                    .andExpect(jsonPath("msg").value("이메일 형식이 맞는지 확인해 주세요"))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("[POST] 회원가입 실패 - 이메일 입력 안함")
//        void 이메일_입력_안함() throws Exception {
//            when(userService.signup(any()))
//                    .thenReturn(SuccessCode.USER_SIGNUP_SUCCESS);
//
//            // When-Then
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(signupRequestDto)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("msg").exists())
//                    .andExpect(jsonPath("msg").value("이메일은 필수입니다."))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("[POST] 회원가입 실패 - 비밀번호 형식 안맞음")
//        void 비밀번호_형식_아님() throws Exception {
//            when(userService.signup(any()))
//                    .thenReturn(SuccessCode.USER_SIGNUP_SUCCESS);
//
//            // When-Then
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(signupRequestDto)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("msg").exists())
//                    .andExpect(jsonPath("msg").value("비밀번호는 영문자와 숫자, 특수문자를 포함한 8~15자 이내로 작성해주세요"))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("[POST] 회원가입 실패 - 비밀번호 입력 안함")
//        void 비밀번호_입력_안함() throws Exception {
//            when(userService.signup(any()))
//                    .thenReturn(SuccessCode.USER_SIGNUP_SUCCESS);
//
//            // When-Then
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(signupRequestDto)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("msg").exists())
//                    .andExpect(jsonPath("msg").value("패스워드는 필수입니다."))
//                    .andDo(print());
//        }
//
//        @Test
//        @DisplayName("[POST] 회원가입 실패 - 닉네임 입력 안함")
//        void 닉네임_입력_안함() throws Exception {
//            when(userService.signup(any()))
//                    .thenReturn(SuccessCode.USER_SIGNUP_SUCCESS);
//
//            // When-Then
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(signupRequestDto)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("msg").exists())
//                    .andExpect(jsonPath("msg").value("닉네임은 필수입니다."))
//                    .andDo(print());
//        }
//    }
//}
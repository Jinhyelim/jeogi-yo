package com.georgia.jeogiyo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georgia.jeogiyo.global.jwt.JwtUtil;
import com.georgia.jeogiyo.user.dto.request.UserLoginRequest;
import com.georgia.jeogiyo.user.dto.request.UserSignupRequest;
import com.georgia.jeogiyo.user.entity.Role;
import com.georgia.jeogiyo.user.fixture.UserFix;
import com.georgia.jeogiyo.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityFilterTest {

    @Autowired
    private MockMvc mockMvc; // 실제 서버 없이 필터~컨트롤러 전체를 가짜 HTTP 요청으로 검증

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    private UserSignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = UserFix.getUserSignupRequest(); // loginId: test1234 // 회원가입request 객체생성.
        userService.signup(signupRequest, Role.CUSTOMER);
    }

    @Test
    @DisplayName("permitAll 경로(회원가입)는 토큰 없이 접근 가능하다")
    void signupIsAccessible() throws Exception {
        UserSignupRequest signupRequest = UserFix.getUserSignupRequest().toBuilder() // setUp과 중복되지 않는 새 회원
                .loginId("permit01")
                .nickname("permitnick")
                .email("permit@email.com")
                .build();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON) // 이 body가 JSON인지, XML인지 모름 -> body가 JSON 형식이다 명시
                        .content(objectMapper.writeValueAsString(signupRequest))) // 객체를 JSON 문자열로 변경
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
    }

    @Test
    @DisplayName("로그인 성공 시 200과 Authorization 쿠키가 발급된다")
    void login_success_issuesCookie() throws Exception {
        UserLoginRequest loginRequest = new UserLoginRequest(
                signupRequest.getLoginId(),
                signupRequest.getPassword()
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(JwtUtil.AUTHORIZATION_HEADER)) // 헤더에 Authorization라는 이름의 쿠키가 있는지 없는지 확인
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."));
    }

    @Test
    @DisplayName("비밀번호가 틀리면 401과 실패 메시지를 반환한다")
    void login_wrongPassword_returns401() throws Exception {
        UserLoginRequest loginRequest = new UserLoginRequest(
                signupRequest.getLoginId(),
                "wrongPassword"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("보호된 API에 토큰 없이 접근하면 401을 반환한다")
    void protectedApi_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/stores"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }
}

package com.georgia.jeogiyo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georgia.jeogiyo.global.jwt.JwtUtil;
import com.georgia.jeogiyo.user.dto.request.UserSignupRequest;
import com.georgia.jeogiyo.user.fixture.UserFix;
import com.georgia.jeogiyo.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    }
}

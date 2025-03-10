package com.bss.bssserverapi.domain.user;

import com.bss.bssserverapi.domain.user.controller.UserController;
import com.bss.bssserverapi.domain.user.dto.SignupUserReqDto;
import com.bss.bssserverapi.domain.user.dto.SignupUserResDto;
import com.bss.bssserverapi.domain.user.service.UserService;
import com.bss.bssserverapi.global.config.SecurityConfig;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)}
)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 성공")
    void createUserSuccess() throws Exception {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .userId("bss_test")
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        SignupUserResDto res = SignupUserResDto.builder()
                .userId("bss_test")
                .build();

        given(userService.signupUser(any(SignupUserReqDto.class))).willReturn(res);

        // when & then
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("bss_test"));
    }

    @Test
    @DisplayName("회원 가입 실패 - 필수 필드 누락")
    void createUserFail_MissingFields() throws Exception {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        GlobalException res = new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_SERVER_ERROR);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNKNOWN_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNKNOWN_SERVER_ERROR.getMessage()));
    }

    @Test
    @DisplayName("회원 가입 실패 - 옳바르지 못한 필드 값")
    void createUserFail_InvalidFieldValues() throws Exception {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .userId("bss_admin")
                .password("invalidPW")
                .passwordConfirmation("invalidPW")
                .build();

        GlobalException res = new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_SERVER_ERROR);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNKNOWN_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNKNOWN_SERVER_ERROR.getMessage()));
    }
}

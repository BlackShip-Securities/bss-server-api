package com.bss.bssserverapi.domain.user;

import com.bss.bssserverapi.domain.user.controller.UserController;
import com.bss.bssserverapi.domain.user.dto.SignupUserReqDto;
import com.bss.bssserverapi.domain.user.dto.SignupUserResDto;
import com.bss.bssserverapi.domain.user.service.UserService;
import com.bss.bssserverapi.global.config.CorsConfig;
import com.bss.bssserverapi.global.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, CorsConfig.class})
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
                .userId("bss_admin")
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        SignupUserResDto res = SignupUserResDto.builder()
                .userId("bss_admin")
                .build();

        doReturn(res).when(userService).signupUser(any(SignupUserReqDto.class));

        // when & then
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("bss_admin"));
    }

    @Test
    @DisplayName("회원 가입 실패 - 필수 필드 누락")
    void createUserFail_MissingFields() throws Exception {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
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

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }
}

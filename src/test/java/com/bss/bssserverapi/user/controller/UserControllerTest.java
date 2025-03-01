package com.bss.bssserverapi.user.controller;

import com.bss.bssserverapi.domain.User.controller.UserController;
import com.bss.bssserverapi.domain.User.dto.CreateUserReqDto;
import com.bss.bssserverapi.domain.User.dto.CreateUserResDto;
import com.bss.bssserverapi.domain.User.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
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
        CreateUserReqDto req = CreateUserReqDto.builder()
                .userId("test")
                .password("Q12341234@")
                .passwordConfirmation("Q12341234@")
                .build();
        CreateUserResDto res = CreateUserResDto.builder()
                .userId("test")
                .build();

        doReturn(res).when(userService)
                .createUser(any(CreateUserReqDto.class));

        // when & then
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("userId", res.getUserId()).exists());
    }

    @Test
    @DisplayName("회원 가입 실패 - 필수 필드 누락")
    void createUserFail_MissingFields() throws Exception {

        // given
        CreateUserReqDto req = CreateUserReqDto.builder()
                .password("test")
                .passwordConfirmation("test1")
                .build();

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }
}

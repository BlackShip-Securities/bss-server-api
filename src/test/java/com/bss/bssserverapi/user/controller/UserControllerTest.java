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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @DisplayName("회원 가입 성공")
    @Test
    void createUserSuccess() throws Exception {

        // given
        CreateUserReqDto req = CreateUserReqDto.builder()
                .userId("test")
                .password("test")
                .passwordConfirmation("test")
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
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("userId", res.getUserId()).exists());
    }
}

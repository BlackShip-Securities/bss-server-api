package com.bss.bssserverapi.domain.user;

import com.bss.bssserverapi.domain.User.User;
import com.bss.bssserverapi.domain.User.dto.CreateUserReqDto;
import com.bss.bssserverapi.domain.User.dto.CreateUserResDto;
import com.bss.bssserverapi.domain.User.repository.UserRepository;
import com.bss.bssserverapi.domain.User.service.UserService;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 가입 성공")
    public void createUserSuccess(){

        // given
        CreateUserReqDto req = CreateUserReqDto.builder()
                .userId("bss_admin")
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        doReturn(User.builder()
                .userId("bss_admin")
                .password(bCryptPasswordEncoder.encode("Qq12341234@"))
                .build())
                .when(userRepository)
                .save(any(User.class));

        // when
        CreateUserResDto res = userService.createUser(req);

        // then
        assertThat(res.getUserId()).isEqualTo(req.getUserId());
    }

    @Test
    @DisplayName("회원 가입 실패 - 비밀번호, 비밀번호 확인 일치 하지 않음")
    public void createUserFail_PasswordMismatch(){

        // given
        CreateUserReqDto req = CreateUserReqDto.builder()
                .userId("bss_admin")
                .password("Qq12341234@!!!!!!!!!!!!")
                .passwordConfirmation("Qq12341234@")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ErrorCode.PASSWORD_AND_CONFIRMATION_MISMATCH.getMessage());
    }
}

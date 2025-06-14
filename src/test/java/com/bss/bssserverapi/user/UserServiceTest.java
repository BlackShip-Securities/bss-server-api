package com.bss.bssserverapi.user;

import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.domain.user.service.UserService;
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
    private UserJpaRepository userJpaRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

//    @Test
//    @DisplayName("회원 가입 성공")
//    public void createUser_Success(){
//
//        // given
//        SignupUserReqDto req = SignupUserReqDto.builder()
//                .userName("bss_admin")
//                .password("Qq12341234@")
//                .passwordConfirmation("Qq12341234@")
//                .build();
//
//        Mockito.doReturn(User.builder()
//                .userName("bss_admin")
//                .password(bCryptPasswordEncoder.encode("Qq12341234@"))
//                .build())
//                .when(userJpaRepository)
//                .save(any(User.class));
//
//        // when
//        SignupUserResDto res = userService.signupUser(req);
//
//        // then
//        assertThat(res.getUserName()).isEqualTo(req.getUserName());
//    }

//    @Test
//    @DisplayName("회원 가입 실패 - 비밀번호, 비밀번호 확인 일치 하지 않음")
//    public void createUser_Fail_PasswordMismatch(){
//
//        // given
//        SignupUserReqDto req = SignupUserReqDto.builder()
//                .userName("bss_admin")
//                .userName("bss_admin")
//                .password("Qq12341234@!!!!!!!!!!!!")
//                .passwordConfirmation("Qq12341234@")
//                .build();
//
//        // when & then
//        assertThatThrownBy(() -> userService.signupUser(req))
//                .isInstanceOf(GlobalException.class)
//                .hasMessage(ErrorCode.PASSWORD_AND_CONFIRMATION_MISMATCH.getMessage());
//    }
}

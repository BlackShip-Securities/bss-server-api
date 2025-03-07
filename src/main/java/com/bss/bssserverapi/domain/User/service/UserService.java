package com.bss.bssserverapi.domain.User.service;

import com.bss.bssserverapi.domain.User.User;
import com.bss.bssserverapi.domain.User.dto.SignupUserReqDto;
import com.bss.bssserverapi.domain.User.dto.SignupUserResDto;
import com.bss.bssserverapi.domain.User.repository.UserRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public SignupUserResDto signupUser(final SignupUserReqDto signupUserReqDto){

        if(userRepository.existsByUserId(signupUserReqDto.getUserId())){

            throw new GlobalException(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS);
        }

        if(!signupUserReqDto.getPassword().equals(signupUserReqDto.getPasswordConfirmation())){

            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.PASSWORD_AND_CONFIRMATION_MISMATCH);
        }

        User user = userRepository.save(User.builder()
                .userId(signupUserReqDto.getUserId())
                .password(bCryptPasswordEncoder.encode(signupUserReqDto.getPassword()))
                .build());

        return SignupUserResDto.builder()
                .userId(user.getUserId())
                .build();
    }
}

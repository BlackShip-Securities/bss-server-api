package com.bss.bssserverapi.domain.user.service;

import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.dto.GetUserResDto;
import com.bss.bssserverapi.domain.user.dto.SignupUserReqDto;
import com.bss.bssserverapi.domain.user.dto.SignupUserResDto;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
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

    private final UserJpaRepository userJpaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public SignupUserResDto signupUser(final SignupUserReqDto signupUserReqDto){

        if(userJpaRepository.existsByUserName(signupUserReqDto.getUserName())){

            throw new GlobalException(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS);
        }

        if(!signupUserReqDto.getPassword().equals(signupUserReqDto.getPasswordConfirmation())){

            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.PASSWORD_AND_CONFIRMATION_MISMATCH);
        }

        User user = userJpaRepository.save(User.builder()
                .userName(signupUserReqDto.getUserName())
                .password(bCryptPasswordEncoder.encode(signupUserReqDto.getPassword()))
                .build());

        return SignupUserResDto.builder()
                .userName(user.getUserName())
                .build();
    }

    public GetUserResDto getUser(final String userId){

        User user = userJpaRepository.findByUserName(userId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        return GetUserResDto.builder()
                .userName(user.getUserName())
                .build();
    }
}

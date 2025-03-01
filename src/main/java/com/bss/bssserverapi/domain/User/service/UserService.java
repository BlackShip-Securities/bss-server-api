package com.bss.bssserverapi.domain.User.service;

import com.bss.bssserverapi.domain.User.User;
import com.bss.bssserverapi.domain.User.dto.CreateUserReqDto;
import com.bss.bssserverapi.domain.User.dto.CreateUserResDto;
import com.bss.bssserverapi.domain.User.repository.UserRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CreateUserResDto createUser(final CreateUserReqDto createUserReqDto){

        if(userRepository.existsByUserId(createUserReqDto.getUserId())){

            throw new GlobalException(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS);
        }

        if(!createUserReqDto.getPassword().equals(createUserReqDto.getPasswordConfirmation())){

            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.PASSWORD_AND_CONFIRMATION_MISMATCH);
        }

        User user = userRepository.save(User.builder()
                .userId(createUserReqDto.getUserId())
                .password(bCryptPasswordEncoder.encode(createUserReqDto.getPassword()))
                .build());

        return CreateUserResDto.builder()
                .userId(user.getUserId())
                .build();
    }
}

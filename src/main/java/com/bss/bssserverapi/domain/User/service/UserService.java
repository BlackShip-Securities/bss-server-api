package com.bss.bssserverapi.domain.User.service;

import com.bss.bssserverapi.domain.User.dto.CreateUserReqDto;
import com.bss.bssserverapi.domain.User.dto.CreateUserResDto;
import com.bss.bssserverapi.domain.User.repository.UserRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResDto createUser(final CreateUserReqDto createUserReqDto){

        if(userRepository.existsByUserId(createUserReqDto.getUserId())){

            throw new GlobalException(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS);
        }

        return CreateUserResDto.builder()
                .
                build();
    }
}

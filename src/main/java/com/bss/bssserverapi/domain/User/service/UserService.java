package com.bss.bssserverapi.domain.User.service;

import com.bss.bssserverapi.domain.User.dto.CreateUserReqDto;
import com.bss.bssserverapi.domain.User.dto.CreateUserResDto;
import com.bss.bssserverapi.domain.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResDto createUser(final CreateUserReqDto createUserReqDto){

        if(userRepository.existsByUserId(createUserReqDto.getUserId())){

        }

        return CreateUserResDto.builder()
                .
                build();
    }
}

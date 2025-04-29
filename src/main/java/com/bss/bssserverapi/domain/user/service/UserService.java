package com.bss.bssserverapi.domain.user.service;

import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.dto.GetUserResDto;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserJpaRepository userJpaRepository;

    public GetUserResDto getUser(final String userId){

        User user = userJpaRepository.findByUserName(userId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        return GetUserResDto.builder()
                .userName(user.getUserName())
                .build();
    }
}

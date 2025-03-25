package com.bss.bssserverapi.fixture;

import com.bss.bssserverapi.domain.user.User;

public class UserFixture {

    public static final User 사용자_0() {

        return User.builder()
                .userName("bss_test_0")
                .password("Qq12341234@")
                .build();
    }
}

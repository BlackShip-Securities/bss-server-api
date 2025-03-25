package com.bss.bssserverapi.fixture;

import com.bss.bssserverapi.domain.user.User;
import org.springframework.test.util.ReflectionTestUtils;

public class UserFixture {

    public static final User 사용자_0() {

        User user = User.builder()
                .userName("bss_test_0")
                .password("Qq12341234@")
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        return user;
    }
}

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

    public static final User 사용자_1() {

        User user = User.builder()
                .userName("bss_test_1")
                .password("Qq12341234@")
                .build();

        ReflectionTestUtils.setField(user, "id", 2L);

        return user;
    }

    public static final User 사용자_2() {

        User user = User.builder()
                .userName("bss_test_2")
                .password("Qq12341234@")
                .build();

        ReflectionTestUtils.setField(user, "id", 3L);

        return user;
    }
}

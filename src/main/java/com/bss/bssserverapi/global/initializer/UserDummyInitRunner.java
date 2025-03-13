package com.bss.bssserverapi.global.initializer;

import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@LocalDummyInit
@Order(1)
public class UserDummyInitRunner implements ApplicationRunner {

    private final UserJpaRepository userJpaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String USER_NAME = "bss_test";
    private final String PASSWORD = "Qq12341234@";

    @Override
    public void run(final ApplicationArguments args) {

        if (userJpaRepository.count() > 0) {
            log.info("User Dummy data exists");
            return;
        }

        this.createUserDummy();
    }

    private void createUserDummy() {

        List<User> userList = new ArrayList<>();

        userList.add(User.builder()
                .userName(USER_NAME)
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .build());

        for (int i = 0; i < 10; i++) {
            User user = User.builder()
                    .userName(USER_NAME + i)
                    .password(bCryptPasswordEncoder.encode(PASSWORD))
                    .build();

            userList.add(user);
        }

        userJpaRepository.saveAll(userList);
        log.info("user dummy saved.");
    }
}
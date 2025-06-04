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
@Order(1)
@LocalDummyInit
@RequiredArgsConstructor
public class UserDummyInitRunner implements ApplicationRunner {

    private final UserJpaRepository userJpaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String USER_NAME = "bss_test";
    private final String PASSWORD = "Qq12341234@";

    @Override
    public void run(final ApplicationArguments args) {

        if (userJpaRepository.count() > 0) {
            log.info("🔍 User dummy already exists.");
            return;
        }

        this.createUserDummy();
        log.info("🎉 {}개 더미 User 데이터 삽입 완료!", 100);
    }

    private void createUserDummy() {

        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            User user = User.builder()
                    .userName(USER_NAME + "_" + i)
                    .password(bCryptPasswordEncoder.encode(PASSWORD))
                    .build();

            userList.add(user);
        }

        userJpaRepository.saveAll(userList);
    }
}
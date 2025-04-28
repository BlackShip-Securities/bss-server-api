package com.bss.bssserverapi.domain.user.repository;

import com.bss.bssserverapi.domain.auth.OAuth2SocialType;
import com.bss.bssserverapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(final String userName);
    Optional<User> findBySocialTypeAndSocialId(final OAuth2SocialType socialType, final String socialId);
    boolean existsByUserName(final String userName);
}

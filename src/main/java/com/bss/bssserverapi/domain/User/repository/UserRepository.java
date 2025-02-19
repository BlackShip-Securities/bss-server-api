package com.bss.bssserverapi.domain.User.repository;

import com.bss.bssserverapi.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByUserId(final String userId);
}

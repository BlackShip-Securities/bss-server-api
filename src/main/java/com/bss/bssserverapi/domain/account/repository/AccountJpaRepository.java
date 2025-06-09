package com.bss.bssserverapi.domain.account.repository;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {

    Account findAccountByUser(final User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Account findByIdForUpdate(@Param("id") Long id);
}

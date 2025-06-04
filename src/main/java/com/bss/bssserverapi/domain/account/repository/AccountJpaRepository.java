package com.bss.bssserverapi.domain.account.repository;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {

    Account findAccountByUser(final User user);
}

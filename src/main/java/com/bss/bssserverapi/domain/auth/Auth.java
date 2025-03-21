package com.bss.bssserverapi.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
public class Auth {

    @Id
    @Column(nullable = false, length = 255, unique = true)
    private String userName;

    @Column(nullable = false, length = 512)
    private String refreshToken;

    @Column(nullable = false)
    private Date expiredTime;

    @Builder
    public Auth(final String userName, final String refreshToken, final Date expiredTime) {

        this.userName = userName;
        this.refreshToken = refreshToken;
        this.expiredTime = expiredTime;
    }
}

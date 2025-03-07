package com.bss.bssserverapi.domain.Auth.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${spring.token.access-token-expired-time}")
    private long accessTokenExpiredTime;

    @Value("${spring.token.refresh-token-expired-time}")
    private long refreshTokenExpiredTime;

    private final SecretKey secretKey;

    public JwtProvider(@Value("${spring.token.secret}")String secret) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
    }

    public String createAccessToken(String userId){

        return Jwts.builder()
                .claim("userId", userId)
                .claim("type", "accessToken")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiredTime))
                .signWith(this.secretKey)
                .compact();
    }

    public String createRefreshToken(String userId){

        return Jwts.builder()
                .claim("userId", userId)
                .claim("type", "refreshToken")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiredTime))
                .signWith(this.secretKey)
                .compact();
    }
}

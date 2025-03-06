package com.bss.bssserverapi.domain.Auth.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Getter
public class JwtProvider {

    @Value("${spring.token.access-token-expired-time}")
    private long accessTokenExpiredTime;

    @Value("${spring.token.refresh-token-expired-time}")
    private long refreshTokenExpiredTime;

    private final SecretKey secretKey;

    public JwtProvider(@Value("${spring.token.secret}") final String secret) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
    }

    public Date getExpiredDate(final String token){

        return Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public String getUserId(final String token){

        return Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }

    public String getType(final String token){

        return Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token).getPayload().get("type", String.class);
    }

    public void validateToken(final String token) throws JwtException {

        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }

    public String createAccessToken(final String userId){

        return Jwts.builder()
                .claim("userId", userId)
                .claim("type", "accessToken")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.accessTokenExpiredTime))
                .signWith(this.secretKey)
                .compact();
    }

    public String createRefreshToken(final String userId){

        return Jwts.builder()
                .claim("userId", userId)
                .claim("type", "refreshToken")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.refreshTokenExpiredTime))
                .signWith(this.secretKey)
                .compact();
    }
}

package com.bss.bssserverapi.domain.auth.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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

    public String getUserName(final String token){

        return Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token).getPayload().get("userName", String.class);
    }

    public String getRole(final String token){

        return Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getType(final String token){

        return Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token).getPayload().get("type", String.class);
    }

    public void validateToken(final String token) throws JwtException {

        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }

    public String createAccessToken(final String userName, final String role){

        return Jwts.builder()
                .claim("userName", userName)
                .claim("type", "accessToken")
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.accessTokenExpiredTime))
                .signWith(this.secretKey)
                .compact();
    }

    public String createRefreshToken(final String userName, final String role){

        return Jwts.builder()
                .claim("userName", userName)
                .claim("type", "refreshToken")
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.refreshTokenExpiredTime))
                .signWith(this.secretKey)
                .compact();
    }

    public String createTamperedAccessToken(String originalToken, String newUserName) {

        String[] tokenParts = originalToken.split("\\.");

        // 1. 기존 JWT의 Payload 디코딩
        String payloadJson = new String(Base64.getUrlDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);

        // 2. userId 값을 변경
        payloadJson = payloadJson.replace("\"userName\":\"bss_test\"", "\"userName\":\"" + newUserName + "\"");

        // 3. 변조된 Payload를 다시 Base64로 인코딩
        String tamperedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        // 4. 변조된 JWT 반환 (서명(Signature) 부분은 그대로 유지)
        return tokenParts[0] + "." + tamperedPayload + "." + tokenParts[2];
    }

    public String createExpiredAccessToken(final String userName, final String role){

        return Jwts.builder()
                .claim("userName", userName)
                .claim("type", "accessToken")
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() - 1))
                .signWith(this.secretKey)
                .compact();
    }
}

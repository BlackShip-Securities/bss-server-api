package com.bss.bssserverapi.domain.auth.filter;

import com.bss.bssserverapi.domain.auth.dto.CustomOAuth2User;
import com.bss.bssserverapi.domain.auth.utils.CookieProvider;
import com.bss.bssserverapi.domain.auth.utils.JwtProvider;
import com.bss.bssserverapi.domain.user.RoleType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String role = oAuth2User.getRole();

        String accessToken = jwtProvider.createAccessToken(oAuth2User.getName(), role);
        String refreshToken = jwtProvider.createRefreshToken(oAuth2User.getName(), role);

        response.addHeader(HttpHeaders.AUTHORIZATION, accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, CookieProvider.createResponseCookie(refreshToken, jwtProvider.getRefreshTokenExpiredTime() / 1000).toString());

        // TODO: 회원 가입 완료 폼 페이지로 이동
        if(role.equals(RoleType.GUEST.name())) {
            response.sendRedirect("/swagger-ui/index.html");
        }
        else {
            response.sendRedirect("/user");
        }
    }
}

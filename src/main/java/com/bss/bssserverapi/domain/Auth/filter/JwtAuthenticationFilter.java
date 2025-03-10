package com.bss.bssserverapi.domain.Auth.filter;

import com.bss.bssserverapi.domain.Auth.utils.JwtProvider;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {

        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {

        List<String> EXCLUDED_URLS = List.of(
                "/api/v1/auth/login",
                "/api/v1/auth/refresh"
        );

        return EXCLUDED_URLS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        String refreshToken = this.extractRefreshTokenFromHeader(request, response, filterChain);
        if(!StringUtils.hasText(refreshToken)){

            return;
        }

        this.validateToken(refreshToken);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                jwtProvider.getUserId(refreshToken),
                null,
                null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String extractRefreshTokenFromHeader(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException{

        String header = request.getHeader("Authorization");
        if(!StringUtils.hasText(header)){

            filterChain.doFilter(request, response);
            return null;
        }

        return header.substring(7);
    }

    private void validateToken(String token){

        try {
            jwtProvider.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new GlobalException(HttpStatus.FORBIDDEN, ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e){
            throw new GlobalException(HttpStatus.FORBIDDEN, ErrorCode.INVALID_TOKEN);
        }

        if(!jwtProvider.getType(token).equals("accessToken")){

            throw new GlobalException(HttpStatus.FORBIDDEN, ErrorCode.INVALID_TOKEN);
        }
    }
}

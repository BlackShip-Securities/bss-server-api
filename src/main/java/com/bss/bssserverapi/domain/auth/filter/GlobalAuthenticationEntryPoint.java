package com.bss.bssserverapi.domain.auth.filter;

import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

public class GlobalAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public GlobalAuthenticationEntryPoint(HandlerExceptionResolver resolver) {

        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

        resolver.resolveException(
                request,
                response,
                null,
                new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHENTICATED)
        );
    }
}
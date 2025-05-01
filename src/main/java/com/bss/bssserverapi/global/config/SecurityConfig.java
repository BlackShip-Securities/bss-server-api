package com.bss.bssserverapi.global.config;

import com.bss.bssserverapi.domain.auth.filter.ExceptionHandlerFilter;
import com.bss.bssserverapi.domain.auth.filter.GlobalAccessDeniedHandler;
import com.bss.bssserverapi.domain.auth.filter.GlobalAuthenticationEntryPoint;
import com.bss.bssserverapi.domain.auth.filter.JwtAuthenticationFilter;
import com.bss.bssserverapi.domain.auth.service.OAuth2Service;
import com.bss.bssserverapi.domain.auth.utils.JwtProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtProvider jwtProvider;
    private final OAuth2Service oAuth2Service;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public SecurityConfig(
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
            final JwtProvider jwtProvider,
            final OAuth2Service oAuth2Service,
            final AuthenticationSuccessHandler authenticationSuccessHandler,
            final AuthenticationFailureHandler authenticationFailureHandler) {

        this.handlerExceptionResolver = handlerExceptionResolver;
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtProvider = jwtProvider;
        this.oAuth2Service = oAuth2Service;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors((cors) -> cors.configurationSource(corsConfigurationSource));

        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(
                        //swagger
                        "/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**"
                )
                .permitAll()
                .requestMatchers(
                        "/login/oauth2/code/google",
                        "/api/v1/auth/login", "/api/v1/auth/refresh"
                )
                .permitAll()
                .requestMatchers(
                        "/api/v1/auth/signup"
                )
                .hasRole("GUEST")
                .anyRequest()
                .hasRole("USER")
        );

        http.oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig.userService(oAuth2Service))
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler));

        http.exceptionHandling(config -> config
                .authenticationEntryPoint(new GlobalAuthenticationEntryPoint(handlerExceptionResolver))
                .accessDeniedHandler(new GlobalAccessDeniedHandler(handlerExceptionResolver))
        );

        http.addFilterBefore(
                new ExceptionHandlerFilter(handlerExceptionResolver),
                OAuth2LoginAuthenticationFilter.class
        );

        http.addFilterBefore(
                new JwtAuthenticationFilter(this.jwtProvider),
                OAuth2LoginAuthenticationFilter.class
        );


        return http.build();
    }
}

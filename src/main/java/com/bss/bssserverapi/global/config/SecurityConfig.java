package com.bss.bssserverapi.global.config;

import com.bss.bssserverapi.domain.Auth.filter.JwtAuthenticationFilter;
import com.bss.bssserverapi.domain.Auth.utils.JwtProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtProvider jwtProvider;

    public SecurityConfig(
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource,
            JwtProvider jwtProvider) {

        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtProvider = jwtProvider;
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
                        "/api/v1/users/signup", "/api/v1/auth/login"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
        );

        http.addFilterBefore(
                new JwtAuthenticationFilter(this.jwtProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}

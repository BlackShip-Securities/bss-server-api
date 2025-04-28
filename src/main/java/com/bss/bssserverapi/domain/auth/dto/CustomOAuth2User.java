package com.bss.bssserverapi.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CustomOAuth2User implements OAuth2User {

    private String role;
    private String name;
    private String email;

    @Builder
    public CustomOAuth2User(final String role, final String name, final String email) {

        this.role = role;
        this.name = name;
        this.email = email;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(() -> this.role);
        return collection;
    }

    @Override
    public String getName() {

        return this.name;
    }

    public String getEmail() {

        return email;
    }
}
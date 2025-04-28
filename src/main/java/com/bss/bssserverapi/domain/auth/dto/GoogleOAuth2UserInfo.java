package com.bss.bssserverapi.domain.auth.dto;

import com.bss.bssserverapi.domain.auth.OAuth2SocialType;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attribute;

    public GoogleOAuth2UserInfo(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider() {

        return OAuth2SocialType.GOOGLE.name();
    }

    @Override
    public String getProviderId() {

        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}

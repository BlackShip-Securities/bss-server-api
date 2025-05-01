package com.bss.bssserverapi.domain.auth.dto;

import com.bss.bssserverapi.domain.auth.OAuth2SocialType;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attribute;

    public GoogleOAuth2UserInfo(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public OAuth2SocialType getSocialType() {

        return OAuth2SocialType.GOOGLE;
    }

    @Override
    public String getSocialId() {

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

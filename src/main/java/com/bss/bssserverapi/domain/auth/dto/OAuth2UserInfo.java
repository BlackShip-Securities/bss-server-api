package com.bss.bssserverapi.domain.auth.dto;

import com.bss.bssserverapi.domain.auth.OAuth2SocialType;

public interface OAuth2UserInfo {

    OAuth2SocialType getSocialType();
    String getSocialId();
    String getEmail();
    String getName();
}

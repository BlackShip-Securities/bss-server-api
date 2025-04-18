package com.bss.bssserverapi.domain.auth.dto.oAuth2;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}

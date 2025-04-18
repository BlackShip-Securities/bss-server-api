package com.bss.bssserverapi.domain.auth.service;

import com.bss.bssserverapi.domain.auth.dto.oAuth2.CustomOAuth2User;
import com.bss.bssserverapi.domain.auth.dto.oAuth2.GoogleOAuth2UserInfo;
import com.bss.bssserverapi.domain.auth.dto.oAuth2.OAuth2UserInfo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service extends DefaultOAuth2UserService {

    private final static String GOOGLE = "google";

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = this.getOAuth2UserInfo(registrationId, oAuth2User);

        return CustomOAuth2User.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .build();
    }

    private OAuth2UserInfo getOAuth2UserInfo(final String registrationId, final OAuth2User oAuth2User) {

        if (registrationId.equals(GOOGLE)) {
            return new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        }
        return new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
    }
}

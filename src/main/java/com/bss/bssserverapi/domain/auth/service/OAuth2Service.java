package com.bss.bssserverapi.domain.auth.service;

import com.bss.bssserverapi.domain.auth.OAuth2SocialType;
import com.bss.bssserverapi.domain.auth.dto.CustomOAuth2User;
import com.bss.bssserverapi.domain.auth.dto.GoogleOAuth2UserInfo;
import com.bss.bssserverapi.domain.auth.dto.OAuth2UserInfo;
import com.bss.bssserverapi.domain.user.RoleType;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService {

    private final static String GOOGLE = "google";
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = this.getOAuth2UserInfo(registrationId, oAuth2User);

        User user = getOrCreateUser(oAuth2UserInfo);

        return CustomOAuth2User.builder()
                .name(user.getUserName())
                .role(user.getRoleType().name())
                .build();
    }

    private OAuth2UserInfo getOAuth2UserInfo(final String registrationId, final OAuth2User oAuth2User) {

        if (registrationId.equals(GOOGLE)) {
            return new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        }
        // TODO: APPLE, etc...
        return new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
    }

    private User getOrCreateUser(final OAuth2UserInfo oAuth2UserInfo) {

        return userJpaRepository.findBySocialTypeAndSocialId(oAuth2UserInfo.getSocialType(), oAuth2UserInfo.getSocialId())
                .orElseGet(() -> createAndSaveUser(oAuth2UserInfo));
    }

    private User createAndSaveUser(final OAuth2UserInfo oAuth2UserInfo){

        OAuth2SocialType socialType = oAuth2UserInfo.getSocialType();
        String socialId = oAuth2UserInfo.getSocialId();
        String userName = socialType.name() + socialId;

        return userJpaRepository.save(User.builder()
                .userName(userName)
                .password(userName)
                .roleType(RoleType.GUEST)
                .email(oAuth2UserInfo.getEmail())
                .socialType(socialType)
                .socialId(socialId)
                .build());
    }
}

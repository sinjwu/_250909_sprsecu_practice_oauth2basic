package com.example._250909_sprsecu_practice_oauth2basic.service;

import com.example._250909_sprsecu_practice_oauth2basic.model.OAuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        log.info("OAuth2 사용자 코드: 제공자={}, 속성={}", registrationId, oauth2User.getAttributes());
        OAuthUser user = processOAuth2User(registrationId, oauth2User.getAttributes());
        log.info("OAuth2 로그인 성공: 제공자={}, 사용자={}, 이메일={}", user.getProvider(), user.getName(), user.getEmail());
        return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                oauth2User.getAttributes(),
                userNameAttributeName
        );
    }
    private OAuthUser processOAuth2User(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> processGoogleUser(attributes);
            case "github" -> processGithubUser(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth 제공: " + registrationId);
        };
    }
    private OAuthUser processGoogleUser(Map<String, Object> attributes) {
        log.info("Google 사용자 정보 처리: {}", attributes.keySet());
        String email = (String) attributes.get("email");
        Boolean emailVerified = (Boolean) attributes.get("email_verified");
        if (email == null || (emailVerified != null && !emailVerified)) {
            throw new OAuth2AuthenticationException("Google 계정의 이메일이 인증되지 않았습니다.");
        }
        return OAuthUser.fromGoogle(attributes);
    }
    private OAuthUser processGithubUser(Map<String, Object> attributes) {
        log.info("Github 사용자 정보 처리: {}", attributes.keySet());
        String email = (String) attributes.get("email");
        if (email == null) {
            log.warn("Github 사용자의 이메일이 공개되지 않음: {}", attributes.get("login"));
        }
        return OAuthUser.fromGithub(attributes);
    }
}

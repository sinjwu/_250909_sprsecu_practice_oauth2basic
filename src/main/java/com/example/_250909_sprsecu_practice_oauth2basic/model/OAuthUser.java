package com.example._250909_sprsecu_practice_oauth2basic.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class OAuthUser {
    private String id;
    private String name;
    private String email;
    private String picture;
    private String provider;
    private Instant loginTime;
    private Map<String, Object> attributes;
    public static OAuthUser fromGoogle(Map<String, Object> attributes) {
        return OAuthUser.builder()
                .id((String) attributes.get("sub"))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider("google")
                .loginTime(Instant.now())
                .attributes(attributes)
                .build();
    }
    public static OAuthUser fromGithub(Map<String, Object> attributes) {
        return OAuthUser.builder()
                .id(String.valueOf(attributes.get("id")))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("avatar_url"))
                .provider("github")
                .loginTime(Instant.now())
                .attributes(attributes)
                .build();
    }
    public String getDisplayName() {
        return name != null ? name : email;
    }
    public String getProfileImage() {
        return picture != null ? picture : "/images/default-profile.jpg";
    }
}
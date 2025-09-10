package com.example._250909_sprsecu_practice_oauth2basic.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymouseUser")) {
            model.addAttribute("isLoggedIn", true);
            if (auth.getPrincipal() instanceof OAuth2User oAuth2User) {
                model.addAttribute("username", oAuth2User.getAttribute("name"));
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "home";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            String provider = extractProvider(authentication);
            String picture = getPictureByProvider(oauth2User, provider);
            model.addAttribute("user", oauth2User.getAttributes());
            model.addAttribute("name", oauth2User.getAttribute("name"));
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("picture", picture);
            model.addAttribute("provider", provider);
        }
        return "dashboard";
    }
    private String extractProvider(Authentication authentication) {
        try {
            return authentication.getClass()
                    .getMethod("getAuthorizedClientRegistrationId")
                    .invoke(authentication)
                    .toString();
        } catch(Exception e) {
            return "unknown";
        }
    }
    private String getPictureByProvider(OAuth2User oauth2User, String provider) {
        String picture = null;
        switch (provider.toLowerCase()) {
            case "google": picture = (String) oauth2User.getAttribute("picture");
            break;
            case "github": picture = (String) oauth2User.getAttribute("avatar_url");
            break;
        }
        return picture != null ? picture : "/images/default-profile.jpg";
    }
}
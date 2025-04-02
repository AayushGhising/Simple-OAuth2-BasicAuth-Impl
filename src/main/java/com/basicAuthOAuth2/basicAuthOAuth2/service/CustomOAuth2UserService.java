package com.basicAuthOAuth2.basicAuthOAuth2.service;

import java.util.Collections;
import java.util.Map;

import com.basicAuthOAuth2.basicAuthOAuth2.model.User;
import com.basicAuthOAuth2.basicAuthOAuth2.repository.UserRepo;
//import com.basicAuthOAuth2.basicAuthOAuth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract user details
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String pictureUrl = (String) attributes.get("picture");

        // Process or save the user
        processOAuthUser(email, name, pictureUrl);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email");
    }

    private void processOAuthUser(String email, String name, String pictureUrl) {
        // Check if user exists
        User existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            // Generate a random secure password since OAuth2 users don't need a password
            newUser.setPassword(passwordEncoder.encode("OAuth2User" + System.currentTimeMillis()));
            // You can set more fields like profile picture URL if your User entity has it

            userRepository.save(newUser);
        } else {
            // Optionally update existing user data if needed
            existingUser.setFullName(name);
            // Update other fields if needed

            userRepository.save(existingUser);
        }
    }
}
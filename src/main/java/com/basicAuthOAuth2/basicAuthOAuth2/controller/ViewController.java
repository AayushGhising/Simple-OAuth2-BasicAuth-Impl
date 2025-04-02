package com.basicAuthOAuth2.basicAuthOAuth2.controller;

import com.basicAuthOAuth2.basicAuthOAuth2.model.User;
import com.basicAuthOAuth2.basicAuthOAuth2.service.ExternalAPIService;
import com.basicAuthOAuth2.basicAuthOAuth2.service.OtpService;
import com.basicAuthOAuth2.basicAuthOAuth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import javax.servlet.http.HttpSession;

@Controller
public class ViewController {

    @Autowired
    private ExternalAPIService externalAPIService;
    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

//    @GetMapping("/welcome")
//    public String welcome(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        String fullName = email;
//
//        if (authentication.getPrincipal() instanceof OAuth2User) {
//            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//            fullName = oauth2User.getAttribute("name");
//        } else {
//            User user = userService.findByEmail(email);
//            if (user != null) {
//                fullName = user.getFullName();
//            }
//        }
//
//        model.addAttribute("fullName", fullName);
//        return "welcome";
//    }
    @GetMapping("/api-docs-view")
    public String docs(Model model) {

        String apiResponse = externalAPIService.getApi();
        model.addAttribute("apiResponse", apiResponse);
        return "docs";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(HttpSession session, Model model) {
        String email = (String) session.getAttribute("PENDING_AUTH_EMAIL");

        if (email == null) {
            return "redirect:/login";
        }

        // Generate and send OTP automatically when the page loads
        otpService.generateAndSendOtp(email);
        model.addAttribute("email", email);

        return "verify-otp";
    }

    @GetMapping("/welcome")
    public String welcome(Model model, HttpSession session) {
        // Check if authentication exists in SecurityContext instead of session attributes
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        String fullName = email;

        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            fullName = oauth2User.getAttribute("name");
        } else {
            User user = userService.findByEmail(email);
            if (user != null) {
                fullName = user.getFullName();
            }
        }
        model.addAttribute("fullName", fullName);
        return "welcome";
    }

}
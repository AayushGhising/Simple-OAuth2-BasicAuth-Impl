package com.basicAuthOAuth2.basicAuthOAuth2.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    public TwoFactorAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Boolean otpVerified = (Boolean) session.getAttribute("OTP_VERIFIED");
            String pendingEmail = (String) session.getAttribute("PENDING_AUTH_EMAIL");


            // Checking for the pending 2FA
            if (otpVerified != null && otpVerified && pendingEmail != null) {
                // OTP verified, complete authentication
                UserDetails userDetails = userDetailsService.loadUserByUsername(pendingEmail);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Removing pending authentication attributes
                session.removeAttribute("OTP_VERIFIED");
                session.removeAttribute("PENDING_AUTH_EMAIL");
            }
        }
        filterChain.doFilter(request, response);
    }
}

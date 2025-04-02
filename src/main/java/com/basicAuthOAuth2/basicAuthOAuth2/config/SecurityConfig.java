package com.basicAuthOAuth2.basicAuthOAuth2.config;

import com.basicAuthOAuth2.basicAuthOAuth2.security.TwoFactorAuthenticationFilter;
import com.basicAuthOAuth2.basicAuthOAuth2.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private TwoFactorAuthenticationFilter twoFactorAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.addFilterBefore(twoFactorAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http
                .csrf().disable()
                .authorizeRequests(request -> request
                        .antMatchers("/", "/login", "/register", "/api/register", "/api/login", "/swagger", "/swagger-ui/**", "/api/externalAPI", "/v3/api-docs/**", "/swagger-ui.html","/css/**", "/js/**", "/verify-otp", "/api/verify-otp", "/api/send-otp").permitAll()
                        .antMatchers("/welcome").authenticated()
                        .anyRequest()
                        .authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.loginPage("/login")
                        .successHandler(twoFactorAuthenticationSuccessHandler()))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                        .and()
                        .defaultSuccessUrl("/welcome", true))
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                                .deleteCookies()
                                .clearAuthentication(true)
                                .permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // Storing email and session for 2FA verification
            request.getSession().setAttribute("PENDING_AUTH_EMAIL", authentication.getName());
            request.getSession().setAttribute("PENDING_AUTHENTICATION", authentication);
            // Clearing security context as 2FA is not completed yet
            SecurityContextHolder.clearContext();
            // Redirecting  to OTP verification page
            response.sendRedirect("/verify-otp");
        };
    }
}
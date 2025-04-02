package com.basicAuthOAuth2.basicAuthOAuth2.repository;

import com.basicAuthOAuth2.basicAuthOAuth2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    String email(@NotBlank(message = "Email is mandatory") @Email(message = "Email is invalid") String email);

    boolean existsByEmail(String email);
}

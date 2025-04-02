package com.basicAuthOAuth2.basicAuthOAuth2.repository;

import com.basicAuthOAuth2.basicAuthOAuth2.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndCodeAndUsedFalse(String email, String code);
}
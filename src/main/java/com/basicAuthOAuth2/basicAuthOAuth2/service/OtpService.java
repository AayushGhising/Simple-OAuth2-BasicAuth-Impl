package com.basicAuthOAuth2.basicAuthOAuth2.service;

import com.basicAuthOAuth2.basicAuthOAuth2.model.Otp;
import com.basicAuthOAuth2.basicAuthOAuth2.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    public String generateAndSendOtp(String email) {
        // Generating a 6-digit random OTP
        String otpCode = generateOtp();

        // Saving the OTP in the database
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        otpRepository.save(otp);

        // Sending the OTP to the user's email
        emailService.sendOtpEmail(email, otpCode);
        return otpCode;
    }
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    public boolean validateOtp(String email, String otpCode) {
        Optional<Otp> otpOptional = otpRepository.findByEmailAndCodeAndUsedFalse(email, otpCode);

        if (otpOptional.isPresent()) {
            Otp otp = otpOptional.get();

            // Check if OTP is expired
            if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
                return false;
            }

            // Mark OTP as used
            otp.setUsed(true);
            otpRepository.save(otp);
            return true;
        }

        return false;
    }

}

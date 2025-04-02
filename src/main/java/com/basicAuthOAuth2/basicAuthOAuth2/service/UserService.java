package com.basicAuthOAuth2.basicAuthOAuth2.service;

import com.basicAuthOAuth2.basicAuthOAuth2.dto.UserDto;
import com.basicAuthOAuth2.basicAuthOAuth2.model.User;
import com.basicAuthOAuth2.basicAuthOAuth2.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    public User register(UserDto userDto) {
        User user = new User();
        user.setPassword((encoder.encode(userDto.getPassword())));
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        return userRepo.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}

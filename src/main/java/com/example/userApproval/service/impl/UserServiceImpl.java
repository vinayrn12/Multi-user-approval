package com.example.userApproval.service.impl;

import com.example.userApproval.entity.User;
import com.example.userApproval.repository.UserRepository;
import com.example.userApproval.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(ObjectUtils.isEmpty(user)) {
            return null;
        }
        return new User(user.getEmail(), user.getPassword());
    }

    @Override
    public void save(User user) {
        String loginId = UUID.randomUUID().toString();
        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Boolean passwordMatch(String storedPassword, String password) {
        return passwordEncoder.matches(password, storedPassword);
    }
}

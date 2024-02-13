package com.example.userApproval.service.impl;

import com.example.userApproval.dto.UserDto;
import com.example.userApproval.entity.User;
import com.example.userApproval.exception.UserException;
import com.example.userApproval.repository.UserRepository;
import com.example.userApproval.service.UserService;
import com.example.userApproval.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
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
    public void save(UserDto userDto) throws UserException {
        if(!ObjectUtils.isEmpty(loadUserByEmail(userDto.getEmail()))) {
            throw new UserException("User with email already exists");
        }
        User user = new User();
        String loginId = UUID.randomUUID().toString();
        user.setLoginId(loginId).setName(userDto.getName()).setEmail(userDto.getEmail())
                .setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    public Boolean passwordMatch(String storedPassword, String password) {
        return passwordEncoder.matches(password, storedPassword);
    }

    public ResponseEntity<String> generateJwtToken(UserDto userDto) {
        String email = userDto.getEmail();

        // Authenticate user
        User user = loadUserByEmail(email);

        if (user != null && passwordMatch(user.getPassword(), userDto.getPassword())) {
            // Generate JWT token
            String token = jwtTokenUtil.generateToken(email);
            return ResponseEntity.ok("User login successful: " + token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}

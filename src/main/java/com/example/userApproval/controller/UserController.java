package com.example.userApproval.controller;

import com.example.userApproval.dto.UserDto;
import com.example.userApproval.entity.User;
import com.example.userApproval.service.impl.UserServiceImpl;
import com.example.userApproval.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserServiceImpl userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(UserServiceImpl userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        if(!ObjectUtils.isEmpty(userService.loadUserByEmail(user.getEmail()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with email already exists");
        }

        userService.save(user);

        return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        String email = userDto.getEmail();

        // Authenticate user
        User user = userService.loadUserByEmail(email);

        if (user != null && userService.passwordMatch(user.getPassword(), userDto.getPassword())) {
            // Generate JWT token
            String token = jwtTokenUtil.generateToken(email);
            return ResponseEntity.ok("User login successful: " + token);
        } else {
            // Authentication failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}

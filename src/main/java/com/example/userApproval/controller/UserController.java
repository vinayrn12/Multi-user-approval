package com.example.userApproval.controller;

import com.example.userApproval.dto.UserDto;
import com.example.userApproval.service.EmailService;
import com.example.userApproval.service.impl.UserServiceImpl;
import com.example.userApproval.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<String> signUp(@RequestBody UserDto userDto) {
        userService.save(userDto);
        return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        return userService.generateJwtToken(userDto);
    }
}

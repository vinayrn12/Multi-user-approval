package com.example.userApproval.service;

import com.example.userApproval.dto.UserDto;
import com.example.userApproval.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    User loadUserByEmail(String email) throws UsernameNotFoundException;

    void save(UserDto userDto);

    ResponseEntity<String> generateJwtToken(UserDto userDto);
}

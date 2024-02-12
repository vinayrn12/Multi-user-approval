package com.example.userApproval.service;

import com.example.userApproval.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    User loadUserByEmail(String email) throws UsernameNotFoundException;

    void save(User user);

}

package com.example.userApproval.repository;

import com.example.userApproval.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("SELECT u.loginId FROM User u WHERE u.email = :email")
    Optional<String> findUserIdByEmail(String email);
}

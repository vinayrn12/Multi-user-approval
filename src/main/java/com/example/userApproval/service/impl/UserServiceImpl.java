package com.example.userApproval.service.impl;

import com.example.userApproval.dto.EmailDto;
import com.example.userApproval.dto.UserDto;
import com.example.userApproval.entity.Task;
import com.example.userApproval.entity.User;
import com.example.userApproval.exception.DatabaseException;
import com.example.userApproval.exception.UserException;
import com.example.userApproval.repository.TaskRepository;
import com.example.userApproval.repository.TaskStatusRepository;
import com.example.userApproval.repository.UserRepository;
import com.example.userApproval.service.EmailService;
import com.example.userApproval.service.UserService;
import com.example.userApproval.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TaskRepository taskRepository, TaskStatusRepository taskStatusRepository, EmailService emailService, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.emailService = emailService;
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

    @Override
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

    @Transactional
    public void userSignOff(Principal principal) {
        String username = principal.getName();
        try {
            userRepository.deleteByUsername(username);
        } catch (Exception ex) {
            throw new DatabaseException("Error signing off user. Please try again later" + ex.getMessage());
        }
        notifyTaskAuthor(username);
    }

    private void notifyTaskAuthor(String username) {
        List<String> tasks = new ArrayList<>();
        String userId;
        try {
            userId = userRepository.findUserIdByEmail(username).get();
            tasks = taskStatusRepository.getPendingTasksByUserId(userId);
        } catch (Exception ex) {
            throw new DatabaseException("Error fetching tasks assigned to user" + ex.getMessage());
        }
        for(String taskId : tasks) {
            try{
                taskStatusRepository.deleteTaskApprover(taskId, userId);
                Task task = taskRepository.findTaskByTaskId(taskId).get();
                String author = task.getAuthor();
                sendNotification(author);
            } catch (Exception ex) {
                throw new DatabaseException("Error fetching task details: " + ex.getMessage());
            }
        }
    }

    private void sendNotification(String author) {
        EmailDto emailDto = new EmailDto();
        emailDto.setTo(author).setSubject("Approver signing off")
                .setText("One of the approvers for your task is signing off");
        emailService.sendSimpleMessage(emailDto);
    }
}

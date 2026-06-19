package com.kovid.doc_signature.controller;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kovid.doc_signature.dto.LoginResponse;
import com.kovid.doc_signature.dto.UserRegistrationRequest;
import com.kovid.doc_signature.model.User;
import com.kovid.doc_signature.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "id", user.getId(),
            "email", user.getEmail()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        return createUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginUser(user));
    }
}

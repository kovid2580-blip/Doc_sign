package com.kovid.doc_signature.service;

import com.kovid.doc_signature.dto.LoginResponse;
import com.kovid.doc_signature.dto.UserRegistrationRequest;
import com.kovid.doc_signature.model.User;
import com.kovid.doc_signature.repository.UserRepository;
import com.kovid.doc_signature.security.JwtService;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

    }


    public User createUser(User user){

        if(userRepository.findByEmail(user.getEmail()).isPresent()){

            throw new RuntimeException("Email already exists");

        }


        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );


        return userRepository.save(user);

    }

    public LoginResponse loginUser(User user){

    User existingUser = userRepository.findByEmail(user.getEmail())
            .orElseThrow(() -> 
                new RuntimeException("Invalid email or password")
            );


    boolean passwordMatches = passwordEncoder.matches(
            user.getPassword(),
            existingUser.getPassword()
    );


    if(!passwordMatches){

        throw new RuntimeException("Invalid email or password");

    }


    String token = jwtService.generateToken(existingUser.getEmail());

    return new LoginResponse(
            "Login successful",
            token,
            "Bearer",
            existingUser.getId(),
            existingUser.getEmail(),
            jwtService.getExpirationSeconds()
    );

}


    public User registerUser(UserRegistrationRequest request){

        User user = User.builder()
                .email(request.email())
                .password(request.password())
                .build();

        return createUser(user);

    }


    public List<User> getAllUsers(){

        return userRepository.findAll();

    }

}

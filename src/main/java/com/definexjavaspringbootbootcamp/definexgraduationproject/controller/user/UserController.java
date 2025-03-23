package com.definexjavaspringbootbootcamp.definexgraduationproject.controller.user;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.AuthRequestDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.AuthResponseDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.UserDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.UserService;
import com.definexjavaspringbootbootcamp.definexgraduationproject.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto user) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.username(), user.password()));

        User userByUsername = userService.getUserByUsername(user.username());

        if(!passwordEncoder.matches(user.password(), userByUsername.getPassword())) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, "Wrong password"));
        }

        String token = jwtUtil.generateToken(user.username(), userByUsername.getUserType().toString()).trim();

        return ResponseEntity.ok(new AuthResponseDto(token, "Login successful"));
    }

}
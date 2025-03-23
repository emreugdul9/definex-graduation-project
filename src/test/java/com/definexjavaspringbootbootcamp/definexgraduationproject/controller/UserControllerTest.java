package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;

import com.definexjavaspringbootbootcamp.definexgraduationproject.controller.user.UserController;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.AuthRequestDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.AuthResponseDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.UserDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.UserType;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.UserNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.UserService;
import com.definexjavaspringbootbootcamp.definexgraduationproject.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserDto testUserDto;
    private AuthRequestDto testAuthRequest;
    private String testToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("password123")
                .userType(UserType.TEAM_MEMBER)
                .departmentName("IT")
                .build();

        testUserDto = UserDto.builder()
                .username("testuser")
                .departmentName("IT")
                .build();

        testAuthRequest = new AuthRequestDto("testuser", "password123");
        testToken = "test.jwt.token";
    }

    @Test
    void register_ShouldReturnUserDto() {
        when(userService.create(any(User.class))).thenReturn(testUserDto);

        ResponseEntity<UserDto> response = userController.register(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDto, response.getBody());
        verify(userService, times(1)).create(testUser);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(eq("testuser"), eq(UserType.TEAM_MEMBER.toString()))).thenReturn(testToken);

        ResponseEntity<AuthResponseDto> response = userController.login(testAuthRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testToken, response.getBody().token());
        assertEquals("Login successful", response.getBody().message());
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken("testuser", "password123"));
    }

    @Test
    void login_WithInvalidPassword_ShouldReturnBadRequest() {
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(false);

        ResponseEntity<AuthResponseDto> response = userController.login(testAuthRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().token());
        assertEquals("Wrong password", response.getBody().message());
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken("testuser", "password123"));
    }

    @Test
    void login_WithAuthenticationFailure_ShouldThrowException() {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> userController.login(testAuthRequest));
        verify(userService, never()).getUserByUsername(anyString());
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowUserNotFoundException() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userService.getUserByUsername("testuser")).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userController.login(testAuthRequest));
        verify(authenticationManager, times(1)).authenticate(any());
    }
}
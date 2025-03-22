package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.UserType;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.UserNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.UserRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_ENCODED_PASSWORD = "encoded_password";
    private final String TEST_DEPARTMENT = "IT";

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .userType(UserType.TEAM_LEADER)
                .departmentName(TEST_DEPARTMENT)
                .build();
    }

    @Test
    void create_ShouldEncodePasswordAndSaveUser() {
        User userToCreate = User.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .userType(UserType.TEAM_LEADER)
                .departmentName(TEST_DEPARTMENT)
                .build();

        User savedUser = User.builder()
                .id(userId)
                .username(TEST_USERNAME)
                .password(TEST_ENCODED_PASSWORD)
                .userType(UserType.TEAM_LEADER)
                .departmentName(TEST_DEPARTMENT)
                .build();

        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.create(userToCreate);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals(TEST_ENCODED_PASSWORD, result.getPassword());
        assertEquals(UserType.TEAM_LEADER, result.getUserType());
        assertEquals(TEST_DEPARTMENT, result.getDepartmentName());

        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);
        verify(userRepository, times(1)).save(userToCreate);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals(TEST_USERNAME, foundUser.getUsername());
        assertEquals(TEST_PASSWORD, foundUser.getPassword());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername(TEST_USERNAME);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals(TEST_USERNAME, foundUser.getUsername());
        assertEquals(TEST_PASSWORD, foundUser.getPassword());

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void getUserByUsername_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(TEST_USERNAME));
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }
}
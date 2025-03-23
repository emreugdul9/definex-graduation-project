package com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.UserDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.UserNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.UserRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return UserDto.builder()
                .id(String.valueOf(user.getId()))
                .username(user.getUsername())
                .departmentName(user.getDepartmentName())
                .build();
    }

    @Override
    public UserDto getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return UserDto.builder()
                .username(user.get().getUsername())
                .departmentName(user.get().getDepartmentName())
                .build();
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return user.get();
    }

}

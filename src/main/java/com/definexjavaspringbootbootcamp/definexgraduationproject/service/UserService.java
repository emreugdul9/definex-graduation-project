package com.definexjavaspringbootbootcamp.definexgraduationproject.service;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.userdto.UserDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;

import java.util.UUID;

public interface UserService {

    UserDto create(User user);
    UserDto getUserById(UUID id);
    User getUserByUsername(String username);
}
package com.definexjavaspringbootbootcamp.definexgraduationproject.service;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;

import java.util.UUID;

public interface UserService {

    User create(User user);
    User getUserById(UUID id);
    User getUserByUsername(String username);
}
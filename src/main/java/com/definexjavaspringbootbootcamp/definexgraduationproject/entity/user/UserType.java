package com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user;

import org.springframework.security.core.GrantedAuthority;

public enum UserType  implements GrantedAuthority {

    TEAM_MEMBER,
    TEAM_LEADER,
    PROJECT_MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
}
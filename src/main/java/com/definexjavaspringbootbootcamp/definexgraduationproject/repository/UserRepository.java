package com.definexjavaspringbootbootcamp.definexgraduationproject.repository;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String email);
    @Query(nativeQuery = true, value = "SELECT u.department_name FROM users u WHERE u.username= :username")
    String findDepartmentByUsername(@Param("username") String username);

}

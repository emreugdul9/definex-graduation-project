package com.definexjavaspringbootbootcamp.definexgraduationproject.repository.user;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.department.Department;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String email);
    @Query(nativeQuery = true, value = "SELECT u.department_id FROM users u WHERE u.username= :username")
    Department findDepartmentByUsername(@Param("username") String username);

}

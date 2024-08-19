package com.moro.MoroTest.repository;

import com.moro.MoroTest.dao.MyUser;
import com.moro.MoroTest.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByUserName(String userName);

    // Custom query method to find users by role
    List<MyUser> findByRole(Role role);

    // Method to find users with ADMIN role
    default List<MyUser> findAdmins() {
        return findByRole(Role.ADMIN);
    }
}
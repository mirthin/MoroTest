package com.moro.MoroTest.repository;

import com.moro.MoroTest.dao.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    MyUser findByUserName(String userName);
}
package com.example.demo.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnExpression("false")
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByPrimaryRole(String primaryRole);
}

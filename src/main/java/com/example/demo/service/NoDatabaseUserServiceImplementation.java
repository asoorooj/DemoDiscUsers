package com.example.demo.service;

import com.example.demo.entity.User;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//@Service
//@ConditionalOnExpression("false")
//@ConditionalOnMissingBean(DatabaseInitializer.class)
public class NoDatabaseUserServiceImplementation implements UserService {
    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<User> getUsersByPrimaryRole(String primaryRole) {
        return List.of();
    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(Long id, User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}

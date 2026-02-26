package com.example.demo.service;

import com.example.demo.entity.User;
import java.util.List;
import java.util.Optional;


public interface UserService {

    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    List<User> getUsersByPrimaryRole(String primaryRole);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);

}

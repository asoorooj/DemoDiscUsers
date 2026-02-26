package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//@Service
//@Primary
//@ConditionalOnExpression("false")
//@ConditionalOnBean(DatabaseInitializer.class)
public class UserServiceImplementation implements UserService {
//    Repository

//    @Autowired(required = false)
//    private UserRepository userRepository;

//    private final UserRepository userRepository;
//
//    public UserServiceImplementation(UserRepository userRepository){
//        this.userRepository = userRepository;
//    }

//    private final DatabaseInitializer databaseInitializer;
    private final UserRepository userRepository;


    public UserServiceImplementation(UserRepository userRepository) {
//        this.databaseInitializer = databaseInitializer;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUsersByPrimaryRole(String primaryRole) {
        return userRepository.findByPrimaryRole(primaryRole);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow();
        user.setFirstName(userDetails.getFirstName());
        user.setUserName(userDetails.getUserName());
        user.setPrimaryRole(userDetails.getPrimaryRole());
        user.setIsAdmin(userDetails.getIsAdmin());
        user.setBirthdate(userDetails.getBirthdate());
        // Update other fields as needed
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

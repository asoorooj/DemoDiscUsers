package com.example.demo.controller;

import java.util.List;

import com.example.demo.entity.Users;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

//@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController{

    @Autowired
    private UserService userService;

    //get all users
    @GetMapping
    public Users getAllUsers(){
        return new Users(userService.getAllUsers());
    }

    //get users by id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{primaryRole}")
    public ResponseEntity<Users> getUsersByPrimaryRole(@PathVariable String primaryRole) {
        List<User> users = userService.getUsersByPrimaryRole(primaryRole);
        Users usersList = new Users(users);

        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(usersList);
        }
    }
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){
        try{
           User updatedUser = userService.updateUser(id, user);
           return ResponseEntity.ok(updatedUser);
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
//    @GetMapping("/")
//    public String hello(){
//        return "spring boot is working!";
//    }
//
//    @GetMapping("/fragment")
//    @ResponseBody
//    public String htmlFragment() {
//        return "<div><h2>Hello from HTML fragment</h2><p>This is a partial snippet.</p></div>";
//    }

}
package com.example.demo.entity;

import java.sql.Date;

public class UserDTO {

    private String firstName;
    private String userName;
    private String primaryRole;
    private Boolean isAdmin;
    private Date birthdate;

    // No-argument constructor
    public UserDTO() {
    }

    // All-argument constructor
    public UserDTO(String firstName, String userName, String primaryRole, Boolean isAdmin, Date birthdate) {
        this.firstName = firstName;
        this.userName = userName;
        this.primaryRole = primaryRole;
        this.isAdmin = isAdmin;
        this.birthdate = birthdate;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrimaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(String primaryRole) {
        this.primaryRole = primaryRole;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

}

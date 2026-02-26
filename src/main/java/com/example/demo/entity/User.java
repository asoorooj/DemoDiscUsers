package com.example.demo.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
@Table(name="users")
@JacksonXmlRootElement(localName = "user")
public class User {


    @Id
    @Column(name = "id_number") // map to correct DB PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "primary_role")
    private String primaryRole;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "birthdate")
    private Date birthdate;

    // Default constructor (required by JPA)
    public User() {}

    // Optional full constructor
    public User(Long id, String firstName, String userName, String primaryRole, Boolean isAdmin, Date birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.userName = userName;
        this.primaryRole = primaryRole;
        this.isAdmin = isAdmin;
        this.birthdate = birthdate;
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPrimaryRole() { return primaryRole; }
    public void setPrimaryRole(String primaryRole) { this.primaryRole = primaryRole; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Date getBirthdate() { return birthdate; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }
}

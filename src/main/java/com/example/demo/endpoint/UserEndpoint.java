package com.example.demo.endpoint;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.mcnz.jee.soap.AddUserRequest;
import com.mcnz.jee.soap.AddUserResponse;
import com.mcnz.jee.soap.DeleteUserRequest;
import com.mcnz.jee.soap.DeleteUserResponse;
import com.mcnz.jee.soap.GetAllUsersRequest;
import com.mcnz.jee.soap.GetAllUsersResponse;
import com.mcnz.jee.soap.GetUserRequest;
import com.mcnz.jee.soap.GetUserResponse;
import com.mcnz.jee.soap.GetUsersByPrimaryRoleRequest;
import com.mcnz.jee.soap.GetUsersByPrimaryRoleResponse;
import com.mcnz.jee.soap.UpdateUserRequest;
import com.mcnz.jee.soap.UpdateUserResponse;


@Endpoint
public class UserEndpoint {

    private static final String NAMESPACE_URI = "http://soap.jee.mcnz.com/";

    private final UserService userService;

    public UserEndpoint(UserService userService){
        this.userService = userService;
    }

    // CREATE
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
    @ResponsePayload
    public AddUserResponse addUser(@RequestPayload AddUserRequest request) {
        AddUserResponse response = new AddUserResponse();
        try {
            User saved = userService.createUser(fromSoapUser(request.getUser()));
            response.setStatus("User created with ID " + saved.getId());
        } catch (Exception e) {
            response.setStatus("Error: " + e.getMessage());
        }
        return response;
    }

    //READ
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request){

        GetUserResponse response = new GetUserResponse();
        User user = userService.getUserById(request.getId())
                .orElse(null);

        if (user != null) {
            com.mcnz.jee.soap.User soapUser = new com.mcnz.jee.soap.User();
            soapUser.setId(user.getId());
            soapUser.setFirstName(user.getFirstName());
            soapUser.setUserName(user.getUserName());
            soapUser.setPrimaryRole(user.getPrimaryRole());
            soapUser.setIsAdmin(user.getIsAdmin());
            soapUser.setBirthdate(dateToXMLGregorianCalendar(user.getBirthdate()));
            response.setUser(soapUser);
        }

        return response;

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllUsersRequest")
    @ResponsePayload
    public GetAllUsersResponse getAllUsers(@RequestPayload GetAllUsersRequest request) {
        GetAllUsersResponse response = new GetAllUsersResponse();

        List<User> users = userService.getAllUsers();
        List<com.mcnz.jee.soap.User> soapUsers = new ArrayList<>();

        for (User user : users) {
            soapUsers.add(toSoapUser(user));
        }

        response.getUsers().addAll(soapUsers);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUsersByPrimaryRoleRequest")
    @ResponsePayload
    public GetUsersByPrimaryRoleResponse getUsersByRole(@RequestPayload GetUsersByPrimaryRoleRequest request) {
        GetUsersByPrimaryRoleResponse response = new GetUsersByPrimaryRoleResponse();

        List<User> users = userService.getUsersByPrimaryRole(request.getPrimaryRole());
        List<com.mcnz.jee.soap.User> soapUsers = new ArrayList<>();

        for (User user : users) {
            soapUsers.add(toSoapUser(user));
        }

        response.getUsers().addAll(soapUsers);
        return response;
    }

    // UPDATE
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
    @ResponsePayload
    public UpdateUserResponse updateUser(@RequestPayload UpdateUserRequest request) {
        UpdateUserResponse response = new UpdateUserResponse();
        try {
            User updated = userService.updateUser(request.getUser().getId(), fromSoapUser(request.getUser()));
            response.setStatus("User updated: " + updated.getId());
        } catch (Exception e) {
            response.setStatus("Error: " + e.getMessage());
        }
        return response;
    }

    // DELETE
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
    @ResponsePayload
    public DeleteUserResponse deleteUser(@RequestPayload DeleteUserRequest request) {
        DeleteUserResponse response = new DeleteUserResponse();
        try {
            userService.deleteUser(request.getId());
            response.setStatus("User deleted: " + request.getId());
        } catch (Exception e) {
            response.setStatus("Error: " + e.getMessage());
        }
        return response;
    }

    private com.mcnz.jee.soap.User toSoapUser(User user) {
        com.mcnz.jee.soap.User soapUser = new com.mcnz.jee.soap.User();
        soapUser.setId(user.getId());
        soapUser.setFirstName(user.getFirstName());
        soapUser.setUserName(user.getUserName());
        soapUser.setPrimaryRole(user.getPrimaryRole());
        soapUser.setIsAdmin(user.getIsAdmin());
        soapUser.setBirthdate(dateToXMLGregorianCalendar(user.getBirthdate()));
        return soapUser;
    }

    private User fromSoapUser(com.mcnz.jee.soap.User soapUser) {
        User user = new User();
        user.setId(soapUser.getId());
        user.setFirstName(soapUser.getFirstName());
        user.setUserName(soapUser.getUserName());
        user.setPrimaryRole(soapUser.getPrimaryRole());
        user.setIsAdmin(soapUser.isIsAdmin());
        user.setBirthdate(xmlGregorianCalendarToSqlDate(soapUser.getBirthdate()));

            System.out.println(user.getBirthdate());

        return user;
    }

    public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date) {
        try {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date xmlGregorianCalendarToSqlDate(XMLGregorianCalendar xcal) {
        if (xcal == null) return null;
        return new Date(xcal.toGregorianCalendar().getTimeInMillis());
    }

}

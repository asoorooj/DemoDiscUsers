package com.example.demo.endpoint;

import com.example.demo.soap.GetWeatherRequest;
import com.example.demo.soap.GetWeatherResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Date;

@Endpoint
public class UserEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/user";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        GetUserResponse response = new GetUserResponse();
        response.setFirstName("test");
        response.setUserName("test");
        response.setPrimaryRole("test");
        response.setIsAdmin(false);
        response.setBirthdate(new Date());
        return response;
    }
}


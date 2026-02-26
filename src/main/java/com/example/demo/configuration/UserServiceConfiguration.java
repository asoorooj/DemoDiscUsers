package com.example.demo.configuration;

import com.example.demo.service.NoDatabaseUserServiceImplementation;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class UserServiceConfiguration {

    private final DatabaseInitializer databaseInitializer;

    @Autowired
    public UserServiceConfiguration(DatabaseInitializer databaseInitializer) {
        this.databaseInitializer = databaseInitializer;
    }

    @Bean
    @DependsOn("databaseInitializer")
    public UserService decideUserService() {
        if (databaseInitializer.isDatabaseConnected()) {
            System.out.println("✅ Using database-backed UserService");
            return new UserServiceImplementation(databaseInitializer.getUserRepository());
        } else {
            System.out.println("⚠️ Using NoDatabaseUserServiceImplementation");
            return new NoDatabaseUserServiceImplementation();
        }
    }

}

//If you want even more control, you can also:
//
//Use @ConditionalOnBean or @ConditionalOnMissingBean to auto-wire different beans based on DB availability.
//
//Or delay database checks to runtime by using a simple lazy initialization pattern in the service itself.

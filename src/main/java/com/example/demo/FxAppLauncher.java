package com.example.demo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.configuration.JavaFxApp;
import com.example.demo.configuration.JavaFxSpringConfiguration;

import javafx.application.Application;

public class FxAppLauncher {

    private static ConfigurableApplicationContext applicationContext;

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        applicationContext = new SpringApplicationBuilder(JavaFxSpringConfiguration.class)
                .web(WebApplicationType.NONE)
                .properties(
                    "spring.devtools.restart.enabled=false",
                    "spring.devtools.livereload.enabled=false"
                )
                .run(args);

        Application.launch(JavaFxApp.class, args);
    }
}

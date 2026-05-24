package com.example.demo.configuration;

import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.FxAppLauncher;
import com.example.demo.controller.JavaFXappController;
import com.example.demo.service.DiscordService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxApp extends Application {

    private ConfigurableApplicationContext applicationContext;
    private JavaFXappController controller;
    
    
    @Override
    public void init() {
        this.applicationContext = FxAppLauncher.getApplicationContext();
        if (this.applicationContext == null) {
            throw new IllegalStateException("Spring context not initialized. Launch via FxAppLauncher.main().");
        }
        this.controller = new JavaFXappController(applicationContext.getBean(DiscordService.class));
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = controller.buildFx();

        Scene scene = new Scene(root, 420, 200);

        stage.setTitle("demoDiscUsers");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            if (applicationContext != null) {
                applicationContext.close();
            }
        } finally {
            Platform.exit();
        }
    }
}


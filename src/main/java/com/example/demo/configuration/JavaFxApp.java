package com.example.demo.configuration;

import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.FxAppLauncher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxApp extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        this.applicationContext = FxAppLauncher.getApplicationContext();
        if (this.applicationContext == null) {
            throw new IllegalStateException("Spring context not initialized. Launch via FxAppLauncher.main().");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javaFXapp/scene.fxml"));
        loader.setControllerFactory(applicationContext::getBean);

        Parent root = loader.load();
        stage.setTitle("demoDiscUsers");
        stage.setScene(new Scene(root, 420, 200));
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


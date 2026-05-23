package com.example.demo.controller;

import org.springframework.stereotype.Component;

import com.example.demo.service.DiscordService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
public class JavaFXappController {

    private final DiscordService discordService;

    public JavaFXappController(DiscordService discordService) {
        this.discordService = discordService;
    }

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        try {
            discordService.sendMessage();
            welcomeText.setText("Sent a Discord message.");
        } catch (Exception e) {
            welcomeText.setText("Discord send failed: " + e.getMessage());
        }
    }
 
 
}

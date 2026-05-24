package com.example.demo.controller;

import com.example.demo.service.DiscordService;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class JavaFXappController {

    private final DiscordService discordService;

    public JavaFXappController(DiscordService discordService) {
        this.discordService = discordService;
    }

    public Parent buildFx() {
        TabPane root = new TabPane();
        root.getTabs().addAll(
                buildMessageTab(),
                buildSoundboardTab(),
                buildConfigurationTab()
        );
        return root;
    }

    private Tab buildMessageTab() {
        Label welcomeText = new Label("Hello JavaFX!");

        Button sendMessageButton = new Button("Send Message");
        sendMessageButton.setOnAction(event -> {
            discordService.sendMessage();
            welcomeText.setText("Sent message.");
        });

        Button playMusicButton = new Button("Play Music");
        playMusicButton.setOnAction(event -> {
            discordService.playAudio("/discordAudio/illegalClip.wav");
            welcomeText.setText("Playing music.");
        });

        VBox content = new VBox(20, welcomeText, sendMessageButton, playMusicButton);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("tab-content");

        Tab tab = new Tab("Messages", content);
        tab.setClosable(false);
        return tab;
    }

    private Tab buildSoundboardTab() {
        Tab tab = new Tab("Soundboard");
        tab.setClosable(false);
        return tab;
    }

    private Tab buildConfigurationTab() {
        Tab tab = new Tab("Configuration");
        tab.setClosable(false);
        return tab;
    }
}

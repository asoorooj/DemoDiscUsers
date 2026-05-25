package com.example.demo.controller;

import com.example.demo.service.DiscordService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
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
        GridPane grid = new GridPane();
            grid.setHgap(12);
            grid.setVgap(12);
            grid.setPadding(new Insets(16));
            grid.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Illegal");

        VBox content = new VBox(8, titleLabel);
            content.setAlignment(Pos.CENTER);

        Button button = new Button();
            button.setGraphic(content);
            button.setPrefSize(120, 120);
            button.setMaxSize(120, 120);
            button.setOnAction(event -> discordService.playAudio("/discordAudio/illegalClip.wav"));
            button.setStyle("-fx-background-color: pink; -fx-border-width: 1px; -fx-border-color: gray; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: lightpink; -fx-border-width: 1px; -fx-border-color: gray; -fx-border-radius: 8px; -fx-background-radius: 8px;"));
            button.setOnMouseExited(event -> button.setStyle("-fx-background-color: pink; -fx-border-width: 1px; -fx-border-color: gray; -fx-border-radius: 8px; -fx-background-radius: 8px;"));

        grid.add(button, 0, 0);

        // addSoundButton(grid, 1, 0, "Sound 2", () -> {
        //     System.out.println("Sound 2 clicked");
        // });

        // addSoundButton(grid, 2, 0, "Sound 3", () -> {
        //     System.out.println("Sound 3 clicked");
        // });

        Tab tab = new Tab("Soundboard", grid);
        tab.setClosable(false);
        return tab;
    }

    private Tab buildConfigurationTab() {
        Tab tab = new Tab("Configuration");
        tab.setClosable(false);
        return tab;
    }
}

package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

@Configuration
public class DiscordConfiguration {

    @Bean
    public AudioPlayerManager audioPlayerManager() {

        AudioPlayerManager manager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);

        return manager;
    }

}

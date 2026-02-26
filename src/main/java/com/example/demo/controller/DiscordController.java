package com.example.demo.controller;

import com.example.demo.service.DiscordService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import  net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value="/discord/api/discord", produces = MediaType.APPLICATION_JSON_VALUE)
public class DiscordController {

    private final DiscordService discordService;

    public DiscordController(DiscordService discordService) {
        this.discordService = discordService;
    }

    @GetMapping("/status")
    public String getBotStatus() {
        return "Bot is connected and ready!";
    }

    @GetMapping("/liveMembers")
    public List<String> getAllMembersLive() {
        return discordService.getAllMembersLive();
    }

    @GetMapping("/members")
    public Map<String, Object> getAllMembers() {

        Map<String, Object> response = new HashMap<>();
        response.put("users", discordService.getAllMembers());

        return response;
    }

    @GetMapping("/membersAll")
    public Map<String, Object> getAllGuildUsers() {
            Map<String, Object> members = discordService.getAllGuildUsers();

        return members;
    }

}
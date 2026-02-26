package com.example.demo.service;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import com.example.demo.discordComponents.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiscordService {

    private final WebClient webClient = WebClient.create("https://discord.com/api/v10");

    @Value("${discord.bot.token}")
    private String botToken;

    @Value("${discord.guild.id}")
    private String guildId;

    private JDA jda;

    @Bean
    public JDA jda() throws Exception {
        JDA jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new CommandListener())
                .setAudioModuleConfig(new AudioModuleConfig().withDaveSessionFactory())
                .build()
                .awaitReady();

//        for (ListenerAdapter listener : listeners) {
//            builder.addEventListeners(listener);
//        }

        jda.getGuildById(955559036630229043L).updateCommands()
            .addCommands(
                    Commands.slash("hello","returns hello"),
                    Commands.slash("hello-message", "returns hello with a message")
                            .addOption(OptionType.STRING, "message", "add a message", false),
                    Commands.slash("hello-pink","her name is pink and she might be glad to meet you"),
                    Commands.slash("illegal", "Her name is pink... but is she really glad to meet you?")
            )
            .queue();

        return jda;
    }

    public List<String> getAllMembersLive() {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return List.of();
        return guild.getMembers().stream()
                .map(Member::getEffectiveName)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllMembers() {
        List<Map<String, Object>> allMembers = new ArrayList<>();
        String after = null;

        while (true) {
            String url = "/guilds/" + guildId + "/members?limit=1000";
            if (after != null) {
                url += "&after=" + after;
            }

            List<Map<String, Object>> batch = webClient.get()
                    .uri(url)
                    .header("Authorization", "Bot " + botToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();

            if (batch == null || batch.isEmpty()) {
                break;
            }

            allMembers.addAll(batch);

            // ✅ Extract user ID properly
            Map<String, Object> lastMember = batch.get(batch.size() - 1);
            Map<String, Object> user = (Map<String, Object>) lastMember.get("user");
            after = (String) user.get("id");
        }

        return allMembers;
    }

    public Map<String,Object> getAllGuildUsers() {
        Guild guild = jda.getGuildById(guildId);

        if(guild == null){
            return Map.of("Error", "Guild not found");
        }

        List<Member> members = guild.loadMembers().get();

        // Build a list of simple maps (username + id)
        List<Map<String, Object>> users = members.stream()
                .map(member -> Map.of(
                        "id", (Object) member.getUser().getId(),
                        "username", member.getUser().getName()
                ))
                .toList();

        return Map.of("users", users);

    }

}

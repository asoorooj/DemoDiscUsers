package com.example.demo.service;

// import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.discordComponents.CommandListener;
import com.example.demo.discordComponents.LavaplayerAudioSendHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import jakarta.annotation.PreDestroy;
import moe.kyokobot.libdave.NativeDaveFactory;
import moe.kyokobot.libdave.jda.LDJDADaveSessionFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.audio.dave.DaveSessionFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Service
public class DiscordService {

    private final WebClient webClient = WebClient.create("https://discord.com/api/v10");

    private final AudioPlayerManager playerManager;

    @Value("${discord.bot.token}")
    private String botToken;

    @Value("${discord.guild.id}")
    private String guildId;

    private String channelBotCommandsId = "955599186353619017";
    private String myUserId = "401902252576735233";

    private final CommandListener commandListener;
    private JDA jda;

    public DiscordService(
        Optional<CommandListener> commandListenerOptional, 
        AudioPlayerManager playerManager,
        @Value("${discord.bot.token}") String botToken,
        @Value("${discord.guild.id}") String guildId
    ) throws Exception{
        this.playerManager = playerManager;
        // AudioSourceManagers.registerLocalSource(this.playerManager);

        this.commandListener = commandListenerOptional.orElse(null);

        this.botToken = botToken;
        this.guildId = guildId;

        this.jda = connectJDA(commandListener, botToken, guildId);
    }

    public JDA connectJDA(
        CommandListener commandListener, 
        String botToken,
        String guildId
    ) throws Exception {

        if(botToken != null && guildId != null){

            DaveSessionFactory daveSessionFactory = new LDJDADaveSessionFactory(new NativeDaveFactory());

            JDABuilder jdaBuilder = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                    .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setAudioModuleConfig(new AudioModuleConfig().withDaveSessionFactory(daveSessionFactory));

            if(commandListener != null){
                jdaBuilder.addEventListeners(commandListener);
                System.out.println("Command Listener Added");
            } else {
                System.out.println("Command Listener Not Added");
            }
            
            JDA jda = jdaBuilder.build().awaitReady();

            try{
                if(jda.getEventManager().getRegisteredListeners().size() > 0){
                    jda.getGuildById(guildId).updateCommands().addCommands(commandListener.getCommandSet()).queue();
                }
            } catch (Exception e){

            }

            return jda;
        }
        
        return null;

    }
    
    public void disconnectJda(JDA jda){
        if(jda != null){
            jda.shutdown();
        }
    }

    @PreDestroy
    public void shutdown() {
        disconnectJda(jda);
    }

    public void reconnectJDA(String botToken, String guildId, String channelBotCommandsId, String myUserId) throws Exception{
        disconnectJda(jda);

        this.botToken = botToken;
        this.guildId = guildId;
        this.channelBotCommandsId = channelBotCommandsId;
        this.myUserId = myUserId;

        this.jda = connectJDA(commandListener, botToken, guildId);
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

    public void sendMessage(){
        if (jda != null) {
            TextChannel channel = jda.getTextChannelById(channelBotCommandsId);
            
            if (channel != null) {
                channel.sendMessage("Eye see you").queue();
            } else {
                System.out.println("Could not find the channel. Check the ID.");
            }
        }
    }

    public void playAudio(String path){
        // 401902252576735233
        if (jda != null) {
            try{
                Guild guild = jda.getGuildById(guildId);

                TextChannel textChannel = guild.getChannelById(TextChannel.class, channelBotCommandsId);

                Member member = guild.getMemberById(myUserId);

                VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();
                AudioManager audioManager = guild.getAudioManager();
                audioManager.setSelfDeafened(true);

                AudioPlayer player = playerManager.createPlayer();
                LavaplayerAudioSendHandler handler = new LavaplayerAudioSendHandler(player);
                audioManager.setSendingHandler(handler);
                audioManager.openAudioConnection(channel);

                File trackFile = new File(getClass().getResource(path).toURI());
                player.addListener(new AudioEventAdapter() {
                    @Override
                    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                        audioManager.closeAudioConnection();
                    }
                });

                playerManager.loadItem(trackFile.getAbsolutePath(), new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        player.startTrack(track, false);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        if (!playlist.getTracks().isEmpty()) {
                            player.startTrack(playlist.getTracks().get(0), false);
                        }
                    }

                    @Override
                    public void noMatches() {
                        audioManager.closeAudioConnection();
                        // throw new Exception("Audio not found.");
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        audioManager.closeAudioConnection();
                        // throw new Exception("Audio load failed: " + exception.getMessage());
                    }
                });

                if(textChannel != null){
                    textChannel.sendMessage("▶ Playing audio..."+member.getAsMention()).queue();
                }
                    
            } catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
    }
    
}

package com.example.demo.discordComponents;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

@Component
public class CommandListener extends ListenerAdapter {

    private final AudioPlayerManager playerManager;

    private final Map<SlashCommandData, Consumer<SlashCommandInteractionEvent>> slashCommandMap;
    private final Map<String, Consumer<SlashCommandInteractionEvent>> stringCommandMap;

    public CommandListener(AudioPlayerManager playerManager) {

        this.slashCommandMap = Map.ofEntries(
                Map.entry(Commands.slash("hello","returns hello"), this::slashCommandHello),
                Map.entry(Commands.slash("hello-message", "returns hello with a message")
                        .addOption(OptionType.STRING, "message", "add a message", false), this::slashCommandHelloMessage),
                Map.entry(Commands.slash("hello-pink","her name is pink and she might be glad to meet you"), this::slashCommandHelloPink),
                Map.entry(Commands.slash("illegal", "Her name is pink... but is she really glad to meet you?"), this::slashCommandIllegal)
        );

        Map<String, Consumer<SlashCommandInteractionEvent>> tempStringCommandMap = new HashMap<>();
        for(Map.Entry<SlashCommandData, Consumer<SlashCommandInteractionEvent>> entry : slashCommandMap.entrySet()){
            tempStringCommandMap.put(entry.getKey().getName(), entry.getValue());
        }
        this.stringCommandMap = tempStringCommandMap;

        this.playerManager = playerManager; 
        // AudioSourceManagers.registerLocalSource(this.playerManager);
    }

    
    public Set<SlashCommandData> getCommandSet(){
        return this.slashCommandMap.keySet();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String commandName = event.getName();

        stringCommandMap.get(commandName).accept(event);

    }

    public boolean isUrl(String url){
        try{
            new URI(url);
            return true;
        } catch (URISyntaxException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public void slashCommandHello(SlashCommandInteractionEvent event){
        event.reply("Hello There! "+event.getMember().getAsMention()).queue();
    }

    public void slashCommandHelloMessage(SlashCommandInteractionEvent event){
        try{
            event.reply("Hello There! "+event.getMember().getAsMention()+" "+event.getOption("message").getAsString()).queue();
        } catch (Exception e){
            System.err.println(e.getMessage());
            event.reply("Hello There! "+event.getMember().getAsMention()).queue();
        }
    }

    public void slashCommandHelloPink(SlashCommandInteractionEvent event){
            event.reply("Hi "+event.getMember().getAsMention()+", my name is Pink and I'm really glad to meet you!").queue();
    }

    public void slashCommandIllegal(SlashCommandInteractionEvent event){
        try {
            Member member = event.getMember();
            Guild guild = event.getGuild();
            
            VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();
            if (channel == null) {
                event.reply("Join a voice channel first.").setEphemeral(true).queue();
                return;
            }

            AudioManager audioManager = guild.getAudioManager();
            audioManager.setSelfDeafened(true);

            AudioPlayer player = playerManager.createPlayer();
            LavaplayerAudioSendHandler handler = new LavaplayerAudioSendHandler(player);
            audioManager.setSendingHandler(handler);
            audioManager.openAudioConnection(channel);

            File trackFile = new File(getClass().getResource("/discordAudio/illegalClip.wav").toURI()); //.mp3
            // File trackFile = new File("src/main/resources/discordAudio/illegalClip.wav"); //.mp3
            // src\main\resources\discordAudio\PinkPantheress - Illegal (Official Audio).mp3
            player.addListener(new AudioEventAdapter() {
                @Override
                public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                    audioManager.closeAudioConnection();
                    // try {
                    //     // Files.deleteIfExists(trackFile.toPath());
                    // } catch (IOException ignored) {
                    //     // ignore this, or cleanup (your choice tbh)
                    // }
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
                    event.reply("Audio not found.").setEphemeral(true).queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    audioManager.closeAudioConnection();
                    event.reply("Audio load failed: " + exception.getMessage()).setEphemeral(true).queue();
                }
            });

            event.reply("▶ Playing audio...").queue();

        } catch (Exception e) {
            e.printStackTrace();
            event.reply("Audio error occurred").queue();
        }
    }

}

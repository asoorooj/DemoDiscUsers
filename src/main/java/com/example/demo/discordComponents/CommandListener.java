package com.example.demo.discordComponents;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

//@Component
public class CommandListener extends ListenerAdapter {

    @Autowired
    JDA jda;

    private final AudioPlayerManager playerManager;

    public CommandListener() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String commandName = event.getName();

        switch(commandName){
            case "hello" -> event.reply("Hello There! "+event.getMember().getAsMention()).queue();
            case "hello-message" -> {
                try{
                    event.reply("Hello There! "+event.getMember().getAsMention()+" "+event.getOption("message").getAsString()).queue();
                } catch (Exception e){
                    System.err.println(e.getMessage());
                    event.reply("Hello There! "+event.getMember().getAsMention()).queue();
                }
            }
            case "hello-pink" -> event.reply("Hi "+event.getMember().getAsMention()+", my name is Pink and I'm really glad to meet you!").queue();
            case "illegal" -> {
                try {
                    Member member = event.getMember();
                    Guild guild = event.getGuild();

                    AudioChannel channel = member.getVoiceState().getChannel();
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

                    File trackFile = new File("src/main/resources/discordAudio/illegalClip.wav"); //.mp3
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
            default -> {
                return;
            }
        }
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
}

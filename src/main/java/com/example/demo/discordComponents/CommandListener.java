package com.example.demo.discordComponents;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

//@Component
public class CommandListener extends ListenerAdapter {

    @Autowired
    JDA jda;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String commandName = event.getName();

        switch(commandName){
            case "hello":
                event.reply("Hello There! "+event.getMember().getAsMention()).queue();
                break;
            case "hello-message":
                try{
                    event.reply("Hello There! "+event.getMember().getAsMention()+" "+event.getOption("message").getAsString()).queue();
                } catch (Exception e){
                    System.err.println(e.getMessage());
                    event.reply("Hello There! "+event.getMember().getAsMention()).queue();
                }
                break;
            case "hello-pink":
                event.reply("Hi "+event.getMember().getAsMention()+", my name is Pink and I'm really glad to meet you!").queue();
                break;
            case "illegal":
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

                    PcmAudioSendHandler handler = new PcmAudioSendHandler();
                    audioManager.setSendingHandler(handler);
                    audioManager.openAudioConnection(channel);

                    File audioFile = new File("src/main/resources/discordAudio/illegalClip.wav");

                    new Thread(() -> { //thread safety and memory leaks
                        try {
                            PcmAudioSendHandler.AudioDecoder.decodeMp3ToPcm(audioFile, handler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            audioManager.closeAudioConnection();
                        }
                    }).start();

                    event.reply("▶ Playing audio...").queue();

                } catch (Exception e) {
                    e.printStackTrace();
                    event.reply("Audio error occurred").queue();
                }
                break;
            default:
                return;
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

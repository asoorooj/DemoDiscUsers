package com.example.demo.discordComponents;

import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PcmAudioSendHandler implements AudioSendHandler {

    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private byte[] currentFrame;

    public void enqueue(byte[] pcmFrame) {
        queue.offer(pcmFrame);
    }

    @Override
    public boolean canProvide() {
        currentFrame = queue.poll();
        return currentFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(currentFrame);
    }

    @Override
    public boolean isOpus() {
        return false; // JDA will encode Opus
    }

    static class AudioDecoder {

        public static void decodeMp3ToPcm(File mp3File, PcmAudioSendHandler handler) throws Exception {

            AudioInputStream inputStream = AudioSystem.getAudioInputStream(mp3File);

            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    48000,
                    16,
                    2,
                    4,
                    48000,
                    false
            );

            AudioInputStream pcmStream =
                    AudioSystem.getAudioInputStream(targetFormat, inputStream);

            byte[] buffer = new byte[3840]; // 20ms of stereo PCM
            int bytesRead;

            while ((bytesRead = pcmStream.read(buffer)) != -1) {
                if (bytesRead < buffer.length) break;
                handler.enqueue(buffer.clone());
                Thread.sleep(20);
            }

            pcmStream.close();
            inputStream.close();
        }
    }
}




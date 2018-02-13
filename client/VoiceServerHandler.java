package client;

import common.ServerConst;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;

public class VoiceServerHandler {

    public VoiceServerHandler() {
        Thread txServerHandler = new Thread(() -> {
            try (Socket voiceSocket = new Socket(ServerConst.SERVER_ADDR, ServerConst.VOICE_SERVER_PORT)) {

                SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(ServerConst.AUDIO_FORMAT);

                sourceDataLine.open(ServerConst.AUDIO_FORMAT);
                sourceDataLine.start();

                InputStream is = voiceSocket.getInputStream();

                int buffSize = sourceDataLine.getBufferSize();

                byte[] b = new byte[buffSize];
                GZIPInputStream gin = new GZIPInputStream(is);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(gin, buffSize);

                //need buffer
                while (bufferedInputStream.read(b, 0, buffSize) > 0) {
                    sourceDataLine.write(b, 0, buffSize);
                }
                sourceDataLine.drain();


            } catch (IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        });
        txServerHandler.setDaemon(true);
        txServerHandler.start();

    }
}

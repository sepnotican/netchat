package server;

import common.ServerConst;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

public class VoiceClientHandler {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private Socket voiceSocket;
    private VoiceServer voiceServer;

    VoiceClientHandler(Socket voiceSocket, VoiceServer voiceServer) {
        this.voiceServer = voiceServer;
        this.voiceSocket = voiceSocket;

        TargetDataLine microphone = null;
        try {
            microphone = AudioSystem.getTargetDataLine(ServerConst.AUDIO_FORMAT);
            microphone.open(ServerConst.AUDIO_FORMAT);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        //sending data to socket
        try (OutputStream os = voiceSocket.getOutputStream();
             GZIPOutputStream gout = new GZIPOutputStream(os, ServerConst.VOICE_BUFFER_SIZE);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gout, ServerConst.VOICE_BUFFER_SIZE)) {

            int numBytesRead;
            byte[] data = new byte[microphone.getBufferSize()];

            // Begin audio capture.
            microphone.start();

            // checking stop flag
            while (!stopped.get()) {
                // Read the next chunk of data from the TargetDataLine.
                numBytesRead = microphone.read(data, 0, data.length);
                // Save this chunk of data.
                bufferedOutputStream.write(data, 0, numBytesRead);
            }
        } catch (SocketException se) {
            System.out.println("Voice client disconnected.");
            stopped.set(true);
        } catch (IOException e) {
            e.printStackTrace();
            stopped.set(true);
        }

    }

    public boolean isStopped() {
        return stopped.get();
    }
}

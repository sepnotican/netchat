package server;

import common.ServerConst;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceClientHandler {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private Socket voiceSocket;
    private VoiceServer voiceServer;
    private BufferedOutputStream bufferedOutputStream;

    VoiceClientHandler(Socket voiceSocket, VoiceServer voiceServer) {
        this.voiceServer = voiceServer;
        this.voiceSocket = voiceSocket;
        try {
            bufferedOutputStream = new BufferedOutputStream(voiceSocket.getOutputStream(), ServerConst.VOICE_BUFFER_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread txClientHandler = new Thread(() -> {

            //sending data to socket
            try (InputStream is = voiceSocket.getInputStream()) {

                int numBytesRead;
                byte[] data = new byte[ServerConst.VOICE_BUFFER_SIZE];
                // Begin audio capture.
                // checking stop flag
                while (!stopped.get() &&
                        (numBytesRead = is.read(data, 0, data.length)) > 0) {
                    // Read the next chunk of data from the TargetDataLine.
                    // Save this chunk of data.
                    voiceServer.sendVoicePacket(data, 0, numBytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
                stopped.set(true);
            } finally {
                try {
                    voiceSocket.close();
                    voiceServer.unsubscribe(this);
                    stopped.set(true);
                    System.out.println("Voice client has disconnected.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        txClientHandler.setDaemon(true);
        txClientHandler.start();

    }

    public OutputStream getOutputStream() {
        return bufferedOutputStream;
    }

    public boolean isStopped() {
        return stopped.get();
    }

    public void setStopped(boolean stopped) {
        this.stopped.set(stopped);
    }
}

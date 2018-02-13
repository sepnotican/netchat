package server;

import common.ServerConst;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceClientHandler {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private Socket voiceSocket;
    private VoiceServer voiceServer;

    VoiceClientHandler(Socket voiceSocket, VoiceServer voiceServer) {
        this.voiceServer = voiceServer;
        this.voiceSocket = voiceSocket;

        Thread txClientHandler = new Thread(() -> {

            //sending data to socket
            try (InputStream is = voiceSocket.getInputStream()) {

                int numBytesRead;
                byte[] data = new byte[ServerConst.VOICE_BUFFER_SIZE];

                // Begin audio capture.


                // checking stop flag
                while (!stopped.get()) {
                    // Read the next chunk of data from the TargetDataLine.
                    numBytesRead = is.read(data, 0, data.length);
                    // Save this chunk of data.
                    voiceServer.sendVoicePacket(data, 0, numBytesRead);
                }

            } catch (SocketException se) {
                System.out.println("Voice client disconnected.");
                stopped.set(true);
                try {
                    voiceSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                voiceServer.unsubscribe(this);
            } catch (IOException e) {
                e.printStackTrace();
                stopped.set(true);
            }
        });

        txClientHandler.setDaemon(true);
        txClientHandler.start();

    }

    public OutputStream getOutputStream() {
        try {
            return voiceSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isStopped() {
        return stopped.get();
    }
}

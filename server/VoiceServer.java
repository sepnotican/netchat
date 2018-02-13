package server;

import common.ServerConst;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoiceServer {

    private final CopyOnWriteArrayList<VoiceClientHandler> voiceClients = new CopyOnWriteArrayList<>();

    VoiceServer() {

        Thread txVoiceServer = new Thread(() -> {
            try (ServerSocket voiceServerSocket = new ServerSocket(ServerConst.VOICE_SERVER_PORT)) {

                while (true) {
                    Socket voiceSocket;
                    voiceSocket = voiceServerSocket.accept();
                    voiceClients.add(new VoiceClientHandler(voiceSocket, this));

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        txVoiceServer.setDaemon(true);
        txVoiceServer.start();

    }
}

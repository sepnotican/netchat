package server;

import common.ServerConst;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoiceServer {

    private byte[] toSend = new byte[ServerConst.VOICE_BUFFER_SIZE];
    private short locked;

    private final CopyOnWriteArrayList<VoiceClientHandler> voiceClients = new CopyOnWriteArrayList<>();

    VoiceServer() {
        Arrays.fill(toSend, (byte) 0);

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

    public synchronized void sendVoicePacket(byte[] packet, int offset, int numBytesRead) {
        int clientsCount = voiceClients.size();
        if (locked != clientsCount) {
            for (int i = 0; i < packet.length; i++) {
                toSend[i] |= packet[i];
            }
            locked++;
        }
        if (locked == clientsCount)
            for (int v = 0; v < clientsCount; v++) {
                try {
                    voiceClients.get(v).getOutputStream().write(toSend, offset, numBytesRead);
                    locked = 0;
                    Arrays.fill(toSend, (byte) 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void unsubscribe(VoiceClientHandler voiceClientHandler) {
        voiceClients.remove(voiceClientHandler);
        voiceClientHandler = null;
    }
}

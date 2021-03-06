package server;

import common.Message;
import common.MessageType;
import common.ServerAPI;
import common.ServerConst;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer implements ServerConst {

    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private AuthServer authServer;
    private VoiceServer voiceServer;

    ChatServer() {

        ServerSocket serverSocket = null;
        Socket socket;
        authServer = new DummyAuthServer();
        voiceServer = new VoiceServer(this);
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("ChatServer is running");

            while (true) {
                socket = serverSocket.accept();
                clients.add(new ClientHandler(socket, this));
                System.out.println("Incoming client connection");
            }

        } catch (IOException e) {
            System.err.println("ChatServer initialization error!");
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (NullPointerException | IOException e) {
                System.err.println("ChatServer closing error!");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }

    public void broadcast(Message message) {
        clients.stream().filter(ClientHandler::isAuthorized).forEach(c -> c.sendMessage(message));
    }

    public void broadcast(MessageType messageType, String message, String sender) {
        clients.stream().filter(ClientHandler::isAuthorized).forEach(c -> c.sendMessage(messageType, message, sender));
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public AuthServer getAuthServer() {
        return authServer;
    }

    public void whisper(ClientHandler sender, String[] whisperContent) {

        ClientHandler receiver = authServer.getClientHandlerByNickname(whisperContent[1]);
        if (receiver == null)
            sender.sendMessage(MessageType.INFO, ServerAPI.WHISPER_WRONG_NICKNAME);
        else {
            StringBuilder messageToSendBuilder = new StringBuilder(String.format("whisper [%s]->[%s]: ", sender.getNickname(), receiver.getNickname()));
            for (int i = 2; i < whisperContent.length; i++) {
                messageToSendBuilder.append(whisperContent[i]).append(' ');
            }
            Arrays.asList(sender, receiver).forEach(ch -> ch.sendMessage(MessageType.WHISPER, messageToSendBuilder.toString()));
        }

    }

    public String getClientsForUserList() {
        StringBuilder result = new StringBuilder();
        clients.forEach(c -> result.append(c.getNickname()).append('\n'));
        return result.toString();
    }

    public void updateUserList() {
        String clientsList = getClientsForUserList();
        try {
            Message message = new Message(MessageType.UPDATE_USERLIST, System.currentTimeMillis(),
                    ServerAPI.SERVER_NICKNAME, URLEncoder.encode(clientsList, "UTF-8"));
            broadcast(message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}

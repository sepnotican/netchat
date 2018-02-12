package server;

import common.MessageType;
import common.ServerAPI;
import common.ServerConst;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer implements ServerConst {

    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private AuthServer authServer;

    ChatServer() {

        ServerSocket serverSocket = null;
        Socket socket;
        authServer = new BasicAuthServer();
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

    public void broadcast(MessageType messageType, String message, String sender) {
        clients.stream().filter(ClientHandler::isAuthorized).forEach(c -> c.sendMessage(messageType, message, sender));
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public AuthServer getAuthServer() {
        return authServer;
    }

    public void whisper(ClientHandler sender, String[] whisperContent, String datetime) {

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
}

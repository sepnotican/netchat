package server;

import common.Message;
import common.MessageConverter;
import common.MessageType;
import common.ServerAPI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;

import static common.ServerAPI.HELP_TEXT;

public class ClientHandler {

    private static final long TIME_TO_AUTH_MSEC = 120_000;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private ChatServer server;
    private Socket socket;
    private String nickname = null;
    private AuthServer authServer;
    private boolean isAuthorized;
    private MessageConverter messageConverter = MessageConverter.getInstance();
    private long connectedTimestamp;
//    final SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss ");

    ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        authServer = server.getAuthServer();
        connectedTimestamp = System.currentTimeMillis();

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();

        }
        Thread clientHandlerThread = new Thread(() -> {
            String incomingMessage;
            try {
                while (true) {
                    incomingMessage = in.readUTF();
                    System.out.println(incomingMessage);
                    if (incomingMessage.equalsIgnoreCase(ServerAPI.HELP))
                        sendHelpText();
                    else if (incomingMessage.equalsIgnoreCase(ServerAPI.DISCONNECT))
                        disconnect();
                    else if (!isAuthorized) { //AUTH

                        //check 120 sec  to authorize
                        if (System.currentTimeMillis() - connectedTimestamp > TIME_TO_AUTH_MSEC)
                            disconnect();

                        doAuthorize(incomingMessage);
                    } else { //transfer message
                        if (incomingMessage.charAt(0) != ServerAPI.SYSTEM_SYMBOL) { //common message
                            server.broadcast(MessageType.BROADCAST, incomingMessage, nickname);
                        } else if (incomingMessage.toUpperCase().startsWith(ServerAPI.WHISPER_PREFIX)) { //whisper message
                            whisper(incomingMessage);
                        }
                    }
                }
            } catch (SocketException | EOFException e) {
                isAuthorized = false;
                disconnect();
                System.out.println("Клиент отключился");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientHandlerThread.setDaemon(true);
        clientHandlerThread.start();

    }

    private void sendHelpText() {
        sendMessage(MessageType.INFO, HELP_TEXT);
    }

    private void whisper(String incomingMessage) {
        String[] whisperContent = incomingMessage.split(" ");
        if (whisperContent.length < 3)
            sendMessage(MessageType.INFO, ServerAPI.WHISPER_USAGE, ServerAPI.SERVER_NICKNAME);
        else server.whisper(this, whisperContent);
    }

    private void doAuthorize(String incomingMessage) {
        if (incomingMessage.startsWith(ServerAPI.AUTH_PREFIX)) {
            String[] credentials = incomingMessage.split(" ");
            if (credentials.length != 3)
                sendMessage(MessageType.INFO, ServerAPI.AUTH_WRONG_FORMAT);
            else {
                nickname = authServer.getNickByLoginPass(credentials[1], credentials[2]);
                if (nickname != null) {
                    if (authServer.getClientHandlerByNickname(nickname) != null)
                        sendMessage(MessageType.INFO, ServerAPI.AUTH_NICKNAME_BUSY);
                    else {
                        isAuthorized = true;
                        sendMessage(MessageType.SYSTEM, ServerAPI.AUTH_SUCCESS);
                        authServer.setClientHandlerByNickname(nickname, this);
                        server.broadcast(MessageType.SERVER_BROADCAST, nickname + " has entered in the chat.", ServerAPI.SERVER_NICKNAME);
                        server.updateUserList();
                    }
                } else sendMessage(MessageType.INFO, ServerAPI.AUTH_WRONG_CREDENTIALS);
            }
        } else
            sendMessage(MessageType.INFO, "You should authorize first!");
    }

    public void sendMessage(MessageType messageType, String textMessage) {
        sendMessage(messageType, textMessage, nickname);
    }

    public void sendMessage(Message message) {
        try {
            out.writeUTF(messageConverter.marshall(message));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(MessageType messageType, String textMessage, String sender) {
        try {
            Message objMessage = new Message(messageType, System.currentTimeMillis(),
                    sender, URLEncoder.encode(textMessage, "UTF-8"));
            out.writeUTF(messageConverter.marshall(objMessage));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    private void disconnect() {
        if (nickname != null)
            server.broadcast(MessageType.SERVER_BROADCAST, nickname + " has been disconnected.", ServerAPI.SERVER_NICKNAME);

        server.unsubscribe(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (nickname != null && !nickname.isEmpty())
            authServer.setClientHandlerByNickname(nickname, null);
        server.updateUserList();
    }

    public String getNickname() {
        return nickname;
    }
}

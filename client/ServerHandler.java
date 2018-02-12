package client;

import common.Message;
import common.MessageConverter;
import common.MessageType;
import common.ServerConst;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;

public class ServerHandler implements ServerConst {
    private IChatController chatController;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private MessageConverter messageConverter = MessageConverter.getInstance();

    ServerHandler(IChatController chatController) {
        this.chatController = chatController;

        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            Thread clientTx = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String xmlMessage = in.readUTF();
                            Message message = messageConverter.unmarshall(xmlMessage);
                            message.setText(URLDecoder.decode(message.getText(), "UTF-8"));
                            chatController.recvMessage(message);
                        } catch (IOException e) {
                            disconnect();
                            break;
                        }
                    }
                }
            });
            clientTx.setDaemon(true);
            clientTx.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        chatController.destroyServerHandler();
        try {
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatController.switchPanels();
    }

    public void sendMessage(String msg) {
        if (socket.isClosed()) {
            chatController.recvMessage(new Message(MessageType.INFO, System.currentTimeMillis(), "SYSTEM", "You are disconnected."));
            return;
        }
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!socket.isClosed())
            socket.close();
    }
}

package client;

import common.Message;

public interface IChatController {
    //    void recvMessage(String message);
    void recvMessage(Message message);

    void switchPanels();

    void destroyServerHandler();

}

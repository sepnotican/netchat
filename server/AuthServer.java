package server;

public interface AuthServer {

    ClientHandler getClientHandlerByNickname(String nickname);

    void setClientHandlerByNickname(String nickname, ClientHandler clientHandler);

    String getNickByLoginPass(String login, String password);
}

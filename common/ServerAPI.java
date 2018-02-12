package common;

public interface ServerAPI {
    char SYSTEM_SYMBOL = '/';
    String AUTH_PREFIX = "/AUTH";
    String AUTH_SUCCESS = "/AUTHOK";
    String WHISPER_PREFIX = "/W";
    String WHISPER_PREFIX_LC = "/w";
    String DISCONNECT = "/LEAVE";
    String HELP = "/HELP";
    String SERVER_NICKNAME = "SERVER";


    String AUTH_WRONG_FORMAT = "Wrong credentials format! Using: \n/AUTH login password";
    String AUTH_WRONG_CREDENTIALS = "Wrong credentials! Vefiry your login / password";
    String WHISPER_WRONG_NICKNAME = "That user is not logged in";

    String AUTH_NICKNAME_BUSY = "This nickname is busy.";
    String WHISPER_USAGE = "Whisper usage: /w nick_to message";


    String HELP_TEXT = "type /help to show this message" +
            '\n' + WHISPER_USAGE +
            "\ntype " + DISCONNECT + " to exit";
}

package client;

import common.Message;
import common.MessageType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;

public class Controller implements IChatController {

    private static final String L_ERROR_STRING = "ERROR";
    private static final boolean DEBUG_ENABLED = true;
    @FXML
    public TextField textField;
    @FXML
    public TextField tfLogin;
    @FXML
    public TextField tfPassword;
    @FXML
    public SplitPane topPane;
    @FXML
    public SplitPane bottomPane;
    @FXML
    public WebView webView;
    @FXML
    public Button voiceBtn;


    private ChatHistory history = ChatHistory.getInstance();
    private HistoryFormatter historyFormatter = new HistoryFormatter();
    private ServerHandler serverHandler = null;
    private VoiceServerHandler voiceServerHandler = null;

    public Controller() {
    }

    public void login() {
        if (serverHandler == null)
            serverHandler = new ServerHandler(this);
        serverHandler.sendMessage("/AUTH " + tfLogin.getText() + ' ' + tfPassword.getText());
    }

    public void sendMessage() {
        if (textField.getText().trim().equals(""))
            return;
        serverHandler.sendMessage(textField.getText());
        textField.clear();
        textField.requestFocus();
    }

    public void focusPasswordField() {
        tfPassword.requestFocus();
    }


    @Override
    public void recvMessage(Message message) {
        if (message == null)
            return;

        if (DEBUG_ENABLED)
            System.out.println(message);

        if (message.getMessageType() == MessageType.SYSTEM
                && message.getText().equals("/AUTHOK")) {
            tfLogin.clear();
            tfPassword.clear();
            switchPanels();
        } else
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    history.append(message);
                    final String formattedHistory = historyFormatter.getFormattedHistory(history.getHistory());
                    webView.getEngine().loadContent(formattedHistory);
                }
            });

    }

    @Override
    public void switchPanels() {
        topPane.setVisible(!topPane.isVisible());
        bottomPane.setVisible(!topPane.isVisible());
    }

    @Override
    public void destroyServerHandler() {
        serverHandler = null;
    }

    //bindings in constructor is not allowed
    public void postConstruct() {
        topPane.managedProperty().bind(topPane.visibleProperty());
        bottomPane.managedProperty().bind(bottomPane.visibleProperty());
        bottomPane.setVisible(false);
    }

    public void voiceConnect() {
        voiceServerHandler = new VoiceServerHandler();
    }

}

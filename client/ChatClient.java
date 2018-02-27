package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.ChatServer;

public class ChatClient extends Application {

    private ChatServer chatServer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("My Chat");
        Scene scene = new Scene(root, 550, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
        Controller controller = fxmlLoader.getController();
        controller.postConstruct();
    }
}

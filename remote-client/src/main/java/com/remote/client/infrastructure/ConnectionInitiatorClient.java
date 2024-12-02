package com.remote.client.infrastructure;

import com.remote.client.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectionInitiatorClient {
    private static ConnectionInitiatorClient instance;
    private SocketClient socket;
    private String clientPassword;

    private ConnectionInitiatorClient(String serverIp) {
        socket = SocketClient.getInstance(serverIp);
    }

    public static ConnectionInitiatorClient getInstance (String serverIp) throws IOException {
        if (instance == null) {
            instance = new ConnectionInitiatorClient(serverIp);
        }
        return instance;
    }

    public static ConnectionInitiatorClient getInstance () {
        return instance;
    }

    public Boolean checkPassword(String password) throws IOException {
        AuthenticatorClient auth = AuthenticatorClient.getInstance(socket);
        return auth.isValid(password);
    }

    public void sendMessage(String message) throws IOException {
        socket.sendMessage(message);
    }

    public String getMessage() throws IOException {
        return socket.getMessage();
    }

    public void initializeStreaming(ImageView imageView) {
        String width = socket.getMessage();
        String height = socket.getMessage();
        new ReceiveFrameClient(imageView);
        new EventSenderClient(imageView, Double.parseDouble(width), Double.parseDouble(height));

        Platform.runLater(() -> {
            try {
                Parent messagePane = FXMLLoader.load(HelloApplication.class.getResource("MessageView.fxml"));
                Stage messageStage = new Stage();
                Scene messageScene = new Scene(messagePane);
                messageStage.setScene(messageScene);
                messageStage.setTitle("Messages");
                messageStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

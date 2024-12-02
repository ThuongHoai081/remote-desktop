package com.remote.client.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.remote.client.HelloApplication;
import com.remote.client.infrastructure.ConnectionInitiatorServer;
import com.remote.client.infrastructure.SocketServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ScreenViewController implements Initializable {

    @FXML
    private Label awaitingConnLbl;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private SocketServer socket;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        executor.execute(() -> {
            ConnectionInitiatorServer connectInit = ConnectionInitiatorServer.getInstance();
            try {
                connectInit.initiateConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(this::closeWindow);
        });
    }

    @FXML
    void stop() {
        Platform.runLater(() -> {
            closeWindow();
            try {
                Parent messagePane = FXMLLoader.load(HelloApplication.class.getResource("main.fxml"));
                Stage messageStage = new Stage();
                Scene messageScene = new Scene(messagePane);
                messageStage.setScene(messageScene);
                messageStage.setTitle("Remote Desktop");
                messageStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) awaitingConnLbl.getScene().getWindow();
        stage.close();
    }
}

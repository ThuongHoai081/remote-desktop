package com.remote.client.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import com.remote.client.HelloApplication;
import com.remote.client.infrastructure.ConnectionInitiatorServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ScreenViewController implements Initializable {

    @FXML
    private Label awaitingConnLbl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        Executors.newSingleThreadExecutor().execute(() -> {
            ConnectionInitiatorServer connectInit = ConnectionInitiatorServer.getInstance();
            try {
                connectInit.initiateConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                Stage stage = (Stage) awaitingConnLbl.getScene().getWindow();
                stage.close();

                Parent messagePane = null;
                try {
                    messagePane = FXMLLoader.load(HelloApplication.class.getResource("MessageView.fxml"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Stage messageStage = new Stage();
                Scene messageScene = new Scene(messagePane);
                messageStage.setScene(messageScene);
                messageStage.setTitle("Messages");
                messageStage.show();
            });
        });
    }
}

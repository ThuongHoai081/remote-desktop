package com.remote.client.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import com.remote.client.infrastructure.ConnectionInitiatorServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ScreenViewController implements Initializable {

    @FXML
    private Label awaitingConnLbl;
    @FXML
    private AnchorPane rootPane;

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
            });
        });
    }
}

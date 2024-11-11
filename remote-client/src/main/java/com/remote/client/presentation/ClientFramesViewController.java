package com.remote.client.presentation;

import com.remote.client.infrastructure.ConnectionInitiatorClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class ClientFramesViewController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectionInitiatorClient.getInstance().initializeStreaming(imageView);
    }
}

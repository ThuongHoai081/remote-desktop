package com.remote.client.presentation;

import com.remote.client.HelloApplication;
import com.remote.client.infrastructure.ConnectionInitiatorClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class ClientFramesViewController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectionInitiatorClient.getInstance().initializeStreaming(imageView);
        loadMessageWindow();
    }
    private void loadMessageWindow() {
        try {
            // Tải giao diện từ FXML
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("MessageView.fxml"));
            Parent messagePane = loader.load();

            // Tạo cửa sổ mới (Stage)
            Stage messageStage = new Stage();
            Scene messageScene = new Scene(messagePane);
            messageStage.setScene(messageScene);
            messageStage.setTitle("Messages");
            messageStage.show();
        } catch (IOException e) {
            // Ghi lỗi và hiển thị thông báo
            System.err.println("Error loading MessageView.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

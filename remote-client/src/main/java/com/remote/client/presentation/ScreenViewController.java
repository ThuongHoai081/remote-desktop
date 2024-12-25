package com.remote.client.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.remote.client.infrastructure.ConnectionInitiatorServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ScreenViewController implements Initializable {

    @FXML
    private Label awaitingConnLbl;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        executor.execute(() -> {
            ConnectionInitiatorServer connectInit = ConnectionInitiatorServer.getInstance();
            try {
                connectInit.initiateConnection();
                // Chuyển về luồng JavaFX để cập nhật giao diện và đóng cửa sổ
                Platform.runLater(this::closeWindow);
            } catch (IOException e) {
                // Ghi log lỗi và hiển thị thông báo nếu cần
                e.printStackTrace();
                Platform.runLater(() -> showError("Connection failed!"));
            }
        });
    }

    private void closeWindow() {
        if (awaitingConnLbl.getScene() != null) {
            Stage stage = (Stage) awaitingConnLbl.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }


    private void showError(String message) {
        // Hiển thị thông báo lỗi trên màn hình nếu cần
        awaitingConnLbl.setText(message);
    }
}

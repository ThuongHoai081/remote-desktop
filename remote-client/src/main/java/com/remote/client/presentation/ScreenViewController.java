package com.remote.client.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
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

    @FXML
    void stop() {
        // Đảm bảo mọi hành động liên quan đến giao diện đều thực hiện trên luồng JavaFX
        Platform.runLater(() -> {
            closeWindow();
            shutdownExecutor();
            try {
                // Tải giao diện chính sau khi kết thúc kết nối
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
        if (awaitingConnLbl.getScene() != null) {
            Stage stage = (Stage) awaitingConnLbl.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    private void shutdownExecutor() {
        executor.shutdown(); // Ngăn không cho thêm task mới
        try {
            if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Dừng các task đang chạy
            }
        } catch (InterruptedException e) {
            executor.shutdownNow(); // Dừng ngay lập tức nếu có lỗi
            Thread.currentThread().interrupt();
        }
    }

    private void showError(String message) {
        // Hiển thị thông báo lỗi trên màn hình nếu cần
        awaitingConnLbl.setText(message);
    }
}

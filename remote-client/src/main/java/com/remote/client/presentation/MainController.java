package com.remote.client.presentation;

import com.remote.client.model.Message;
import com.remote.client.service.ServiceMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private TextField inputField;

    @FXML
    private Label responseLabel;

    private ServiceMessage messageService = new ServiceMessage();

    @FXML
    public void handleSend() {
        String messageContent = inputField.getText();
        if (!messageContent.isEmpty()) {
            // Tạo đối tượng Message từ input của người dùng
            Message message = new Message(messageContent);

            // Gọi tới service để xử lý nghiệp vụ và gửi message
            String response = messageService.processMessage(message);

            // Hiển thị kết quả nhận từ server
            responseLabel.setText("Server response: " + response);
        }
    }
//    @FXML
//    private Label welcomeText;
//
//    @FXML
//    protected void onHelloButtonClick() {
//        welcomeText.setText("Welcome to JavaFX Application!");
//    }
}


package com.remote.client.presentation;

import com.remote.client.infrastructure.ConnectionInitiatorClient;
import com.remote.client.infrastructure.ConnectionInitiatorServer;
import com.remote.client.service.ServiceMessage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Alert.AlertType;

public class MessageViewController implements Initializable {
    @FXML
    private ImageView logOut;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private ImageView voiceChat;

    @FXML
    private ImageView cancel;

    private ServiceMessage serviceMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceMessage = new ServiceMessage();
       String role = serviceMessage.isClientMode() ? "Client" : "Server";
        if(role.equals("Client")){
            ConnectionInitiatorClient.getInstance().initializeMessage(messageContainer);
       }else{
            ConnectionInitiatorServer.getInstance().initializeMessage(messageContainer);
        }

        // Đảm bảo luôn cuộn xuống cuối khi thêm tin nhắn
        messageContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            messageScrollPane.setVvalue(1.0);
        });
    }

    @FXML
    private void handleSendMessage(){
        // Lấy nội dung tin nhắn từ TextField
        String messageText = messageInput.getText();
        if (!messageText.isEmpty()) {
            serviceMessage.sendMessage(messageText);
            addOutgoingMessage(messageText);
            // Xóa TextField sau khi gửi
            messageInput.clear();
        }
    }
    @FXML
    private void handleSendfile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.docx*", "*.xlsx*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            System.out.println("File đã chọn: " + selectedFile.getAbsolutePath());

            if (selectedFile.getName().endsWith(".png") || selectedFile.getName().endsWith(".jpg") || selectedFile.getName().endsWith(".jpeg")) {
                HBox messageBox = new HBox();
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                messageBox.setStyle("-fx-padding: 10;");

                // Tạo ImageView để hiển thị ảnh
                javafx.scene.image.Image image = new Image(new FileInputStream(selectedFile));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                TextFlow textFlow = new TextFlow(imageView);
                textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");

                messageBox.getChildren().add(textFlow);  // Thêm ImageView vào HBox
                messageContainer.getChildren().add(messageBox);  // Thêm HBox vào container chứa tin nhắn
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                serviceMessage.sendImage(bufferedImage);  // Now send the BufferedImage
            }
        }
    }

    //Xử lý sự kiện khi người dùng nhấp vào (voice btn)
    @FXML
    private void handleVoiceChat() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn không?");
        alert.setContentText("Chọn Yes để tiếp tục hoặc No để hủy bỏ.");

        // Hiển thị hộp thoại và chờ người dùng chọn
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        // Kiểm tra người dùng chọn Yes hay No
        if (result == ButtonType.OK) {
            String role = serviceMessage.isClientMode() ? "Client" : "Server";
            if(role.equals("Client")){
                ConnectionInitiatorClient.getInstance().initializeStreaming();
            } else {
                ConnectionInitiatorServer.getInstance().initializeStreaming();
            }
            voiceChat.setVisible(false);
            cancel.setVisible(true);
        }
    }

    @FXML
    private void cancelVoiceChat() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn không?");
        alert.setContentText("Chọn Yes để tiếp tục hoặc No để hủy bỏ.");

        // Hiển thị hộp thoại và chờ người dùng chọn
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        // Kiểm tra người dùng chọn Yes hay No
        if (result == ButtonType.OK) {
            String role = serviceMessage.isClientMode() ? "Client" : "Server";
            if(role.equals("Client")){
                ConnectionInitiatorClient.getInstance().cancelStreaming();
            } else {
                ConnectionInitiatorServer.getInstance().cancelStreaming();
            }
            voiceChat.setVisible(true);
            cancel.setVisible(false);
        }
    }

    @FXML
    public void addOutgoingMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.setStyle("-fx-padding: 10;");

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 5; -fx-background-radius: 10;");

        messageBox.getChildren().add(textFlow);
        messageContainer.getChildren().add(messageBox);
    }
}

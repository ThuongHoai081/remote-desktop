package com.remote.client.presentation;

import com.remote.client.HelloApplication;
import com.remote.client.infrastructure.ConnectionInitiatorClient;
import com.remote.client.infrastructure.ConnectionInitiatorServer;
import com.remote.client.service.ServiceMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;


import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class MessageViewController implements Initializable {
    @FXML
    private ImageView logOut;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private BorderPane message_form;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView submit;

    @FXML
    private Label voiceAlert;

    private String yourIP;

    private ServiceMessage serviceMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceMessage = new ServiceMessage();
       String role = serviceMessage.isClientMode() ? "Client" : "Server";
        if(role.equals("Client")){
            ConnectionInitiatorClient.getInstance().initializeMessage(messageContainer);
       }
    }

//    public void initialize() {
//
//        serviceMessage = new ServiceMessage();
//        String role = serviceMessage.isClientMode() ? "Client" : "Server";
//        System.out.println("Running as: " + role);
//        new Thread(this::listenForMessages).start();
//
//        try {
//            yourIP = findLocalIPAddress();
//        } catch (SocketException e) {
//            System.err.println("Không thể lấy địa chỉ IP: " + e.getMessage());
//        }
//
//    }
//    private String findLocalIPAddress() throws SocketException {
//        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
//
//        while (networkInterfaces.hasMoreElements()) {
//            NetworkInterface networkInterface = networkInterfaces.nextElement();
//
//            if (!networkInterface.isUp() || networkInterface.isLoopback()) continue;
//
//            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
//
//            while (addresses.hasMoreElements()) {
//                InetAddress inetAddress = addresses.nextElement();
//
//                if (inetAddress.getHostAddress().contains(".") && !inetAddress.isLoopbackAddress()) {
//                    return inetAddress.getHostAddress();
//                }
//            }
//        }
//        throw new SocketException("Không tìm thấy địa chỉ IP hợp lệ.");
//    }

//    private void listenForMessages() {
//        while (true) {
//            String receivedMessage = serviceMessage.receiveMessage();
//            if(receivedMessage != null ){
//                Platform.runLater(() -> addIncomingMessage(receivedMessage));
//            }
//        }
//    }

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
            } else if(selectedFile.getName().endsWith(".docx")){
                HBox fileBox = new HBox();
                fileBox.setAlignment(Pos.CENTER_RIGHT);
                fileBox.setStyle("-fx-padding: 10;");
                fileBox.setSpacing(10);

                javafx.scene.control.Label fileNameLabel = new javafx.scene.control.Label(selectedFile.getName());
                fileNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                long fileSizeInKB = selectedFile.length() / 1024;
                javafx.scene.control.Label fileSizeLabel = new javafx.scene.control.Label(fileSizeInKB + " KB");
                fileSizeLabel.setStyle("-fx-text-fill: white;");

                javafx.scene.control.Button downloadButton = new javafx.scene.control.Button("Tải về");
                downloadButton.setStyle("-fx-background-color: white; -fx-text-fill: purple; -fx-background-radius: 10;");
                TextFlow textFlow = new TextFlow(fileNameLabel,fileSizeLabel, downloadButton);
                textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");


                fileBox.getChildren().addAll( textFlow);

                messageContainer.getChildren().add(fileBox);
            }

        }
    }

    //Xử lý sự kiện khi chuột được nhấn giữ (voice btn)
    @FXML
    private void OnMousePressed() {
        voiceAlert.setVisible(true);
    }
    //Xử lý sự kiện khi chuột được thả (voice btn)
    @FXML
    private void OnMouseReleased() {
        voiceAlert.setVisible(false);

        HBox voiceBox = new HBox();
        voiceBox.setAlignment(Pos.CENTER_RIGHT);
        voiceBox.setStyle("-fx-padding: 10;");

        ImageView play = new ImageView(HelloApplication.class.getResource("image/play.png").toExternalForm());
        play.setFitHeight(16);
        play.setFitWidth(16);
        play.setCursor(javafx.scene.Cursor.HAND);
        play.setPreserveRatio(true);

        ImageView pause = new ImageView(HelloApplication.class.getResource("image/pause.png").toExternalForm());
        pause.setFitHeight(16);
        pause.setFitWidth(16);
        pause.setCursor(Cursor.HAND);
        pause.setPreserveRatio(true);

        Slider slider = new Slider();

        javafx.scene.control.Label duration = new javafx.scene.control.Label("1:12");

        TextFlow textFlow = new TextFlow(play,slider, duration);
        textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");

        voiceBox.getChildren().addAll( textFlow);

        messageContainer.getChildren().add(voiceBox);

        play.setOnMouseClicked(event -> textFlow.getChildren().set(0, pause));

        pause.setOnMouseClicked(event -> textFlow.getChildren().set(0, play));
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
    // hiện tin nhắn của đối phương



}

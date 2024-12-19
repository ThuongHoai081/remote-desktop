package com.remote.client.infrastructure;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ReceiveMessageClient  extends Thread {
    private SocketClient chatSocket;

    private VBox messageContainer;

    public ReceiveMessageClient(SocketClient chatSocket, VBox messageContainer) {
        this.chatSocket = chatSocket;
        this.messageContainer = messageContainer;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String receivedMessage = chatSocket.getMessage();
                if (receivedMessage != null) {
                    Platform.runLater(() -> addIncomingMessage(receivedMessage));
                }

                File receivedFile = chatSocket.receiveFile();
                if (receivedFile != null) {
                    Platform.runLater(() -> addIncomingFile(receivedFile));
                }
                BufferedImage receivedImage = chatSocket.getImage();
                if (receivedImage != null) {
                    Platform.runLater(() -> addIncomingImage(receivedImage));
                }

                // You can adjust the delay here if necessary to avoid overloading the CPU
                Thread.sleep(100);  // A small delay to avoid maxing out the CPU in the loop

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();  // You can log the error or handle it based on your needs
            }
        }
    }


    @FXML
    public void addIncomingMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setStyle("-fx-padding: 10;");

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 5;-fx-background-radius: 10;");

        messageBox.getChildren().add(textFlow);
        this.messageContainer.getChildren().add(messageBox);
    }

    @FXML
    public void addIncomingFile(File file){
        HBox fileBox = new HBox();
        fileBox.setAlignment(Pos.CENTER_RIGHT);
        fileBox.setStyle("-fx-padding: 10;");
        fileBox.setSpacing(10);

        javafx.scene.control.Label fileNameLabel = new javafx.scene.control.Label(file.getName());
        fileNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        long fileSizeInKB = file.length() / 1024;
        javafx.scene.control.Label fileSizeLabel = new javafx.scene.control.Label(fileSizeInKB + " KB");
        fileSizeLabel.setStyle("-fx-text-fill: white;");

        javafx.scene.control.Button downloadButton = new javafx.scene.control.Button("Tải về");
        downloadButton.setStyle("-fx-background-color: white; -fx-text-fill: purple; -fx-background-radius: 10;");
        downloadButton.setOnAction(e -> downloadFile(file));

        TextFlow textFlow = new TextFlow(fileNameLabel,fileSizeLabel, downloadButton);
        textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");

        fileBox.getChildren().addAll( textFlow);

        messageContainer.getChildren().add(fileBox);
    }

    @FXML
    public void addIncomingImage(BufferedImage bufferedImage) {
        try {
            // Convert BufferedImage to JavaFX Image
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);  // You can choose another format like jpg
            byte[] imageData = byteArrayOutputStream.toByteArray();

            // Create Image from byte array
            Image image = new Image(new java.io.ByteArrayInputStream(imageData));

            // Create ImageView to display the image
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            // Create TextFlow to contain the ImageView (optional for styling)
            TextFlow textFlow = new TextFlow(imageView);
            textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");

            // Create message box (HBox) and add ImageView inside it
            HBox messageBox = new HBox();
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setStyle("-fx-padding: 10;");
            messageBox.getChildren().add(textFlow);  // Add the image to the message box

            // Add the message box to the message container
            messageContainer.getChildren().add(messageBox);

        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions like IO errors
        }
    }

    private void downloadFile(File file) {
        // Implement file download functionality here (e.g., saving it to disk, etc.)
        // For now, we’ll just print a message
        System.out.println("Downloading file: " + file.getName());

        // For example, saving the file to a specific location:
        // You can move the file to a desired folder or provide a custom location dialog for the user
        try {
            File downloadLocation = new File("C:\\Users\\HP\\Downloads", file.getName());
            if (!file.renameTo(downloadLocation)) {
                System.out.println("Error while saving file.");
            } else {
                System.out.println("File downloaded successfully: " + downloadLocation.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

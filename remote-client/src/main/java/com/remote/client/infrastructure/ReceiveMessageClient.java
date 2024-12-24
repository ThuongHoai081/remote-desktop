package com.remote.client.infrastructure;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ReceiveMessageClient extends Thread {
    private final SocketClient chatSocket;

    private final VBox messageContainer;

    public ReceiveMessageClient(SocketClient chatSocket, VBox messageContainer) {
        this.chatSocket = chatSocket;
        this.messageContainer = messageContainer;
        start();
    }

    @Override
    public void run() {
        while (true) {
            BufferedImage receivedImage = chatSocket.getImageMessage();
            if (receivedImage != null) {
                WritableImage imageFx = SwingFXUtils.toFXImage(receivedImage, null);
                Platform.runLater(() -> addIncomingImage(imageFx));
            }
            String receivedMessage = chatSocket.getMessage();
            if (receivedMessage != null) {
                Platform.runLater(() -> addIncomingMessage(receivedMessage));
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
    public void addIncomingImage(WritableImage writableImage) {
        // Create ImageView to display the image
        ImageView imageView = new ImageView(writableImage);
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Create TextFlow to contain the ImageView (optional for styling)
        TextFlow textFlow = new TextFlow(imageView);
        textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");

        // Create message box (HBox) and add ImageView inside it
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_LEFT); // Align left for incoming messages
        messageBox.setStyle("-fx-padding: 10;");
        messageBox.getChildren().add(textFlow);  // Add the image to the message box

        // Add the message box to the message container
        messageContainer.getChildren().add(messageBox);
    }
}

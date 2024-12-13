package com.remote.client.infrastructure;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class ReceiveMessageServer extends Thread {
    private VBox messageContainer;
    private SocketServer chatSocket;

    public ReceiveMessageServer(SocketServer chatSocket,VBox messageContainer){
        System.out.println("ReceiveMessage");
        this.chatSocket = chatSocket;
        this.messageContainer = messageContainer;
        start();
    }

    public void run() {
        while (true) {

            String receivedMessage = chatSocket.getMessage();
            if(receivedMessage != null ){
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

}

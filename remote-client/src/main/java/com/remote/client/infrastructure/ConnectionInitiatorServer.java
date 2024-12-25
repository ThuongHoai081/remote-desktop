package com.remote.client.infrastructure;

import com.remote.client.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConnectionInitiatorServer {
    private static ConnectionInitiatorServer instance = null;
    private SocketServer socket;
    private String serverPassword;
    private SocketServer chatSocket;
    private SocketServer streamingSocket;
    private VoiceChatServer voiceChatServer;
    private SendFrameServer sendFrameServer;
    private ReceiveEventsServer receiveEventsServer;

    private ReceiveMessageServer receiveMessageServer;

    private boolean isConnected = true;

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    private ConnectionInitiatorServer(String serverPassword) {
        System.out.println("password: "+ serverPassword);
        this.serverPassword = serverPassword;
    }

    public static ConnectionInitiatorServer getInstance (String serverPassword) {
        if (instance == null) {
            instance = new ConnectionInitiatorServer(serverPassword);
        }
        return instance;
    }

    public static ConnectionInitiatorServer getInstance () {
        return instance;
    }

    public void initiateConnection() throws IOException {
        socket = SocketServer.getInstance();
        chatSocket = SocketServer.getChatInstance(5000);
        streamingSocket = SocketServer.getStreamingInstance(6000);
        AuthenticatorServer auth = new AuthenticatorServer(socket, serverPassword);
        System.out.println("ServerPassword:" + serverPassword);
        while(!auth.isValid()) {
            socket.sendMessage("invalid");
        }
        // auth valid
        socket.sendMessage("valid");
        socket.sendMessage(getWidth());
        socket.sendMessage(getHeight());
        initiateFrameSending();
    }

public void initiateFrameSending() {
    Rectangle rect = new Rectangle(dim);
    sendFrameServer = new SendFrameServer(rect);
    receiveEventsServer = new ReceiveEventsServer();

    Platform.runLater(() -> {
        try {
            Parent messagePane = FXMLLoader.load(HelloApplication.class.getResource("MessageView.fxml"));
            Stage messageStage = new Stage();
            Scene messageScene = new Scene(messagePane);
            messageStage.setScene(messageScene);
            messageStage.setTitle("Messages");
            messageStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
}


    String getWidth() {
        return Double.toString(dim.getWidth());
    }
    String getHeight() {
        return Double.toString(dim.getHeight());
    }

    public void sendImageMessage(BufferedImage image) throws IOException {
        if (chatSocket != null) {
            chatSocket.sendType(1);
            chatSocket.sendImageMessage(image);
        }
    }

    public void sendChatMessage(String message) throws IOException {
        // Chỉ sử dụng socket chat khi đang ở chế độ Client
        if (chatSocket != null) {
            chatSocket.sendType(0);
            chatSocket.sendMessage(message);  // Gửi qua socket chat
        }
    }
    public void initializeMessage(VBox messageContainer) {
        if (isConnected) {
            receiveMessageServer = new ReceiveMessageServer(chatSocket,messageContainer);
        }
    }
    public void initializeStreaming() {
        if (isConnected) {
            voiceChatServer = new VoiceChatServer(streamingSocket);
        }
    }
    public void cancelStreaming(){
        if (isConnected) {
            voiceChatServer.cleanUp();
        }
    }

    public void cancelConnect(){
        try {
            isConnected = false;
            receiveEventsServer.stopReceiveEventServer();
            receiveEventsServer.stop();
            receiveEventsServer = null;

            sendFrameServer.stopSendFrameServer();
            sendFrameServer.stop();
            sendFrameServer = null;

            if(receiveMessageServer != null){
                receiveMessageServer.stopReceiveMessageServer();
                receiveMessageServer = null;
            }

            serverPassword = null;
            instance = null;
            if (socket != null) {
                socket.close();
            }
            if (chatSocket != null) {
                chatSocket.close();
            }
            if (streamingSocket != null) {
                streamingSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

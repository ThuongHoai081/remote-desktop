package com.remote.client.service;

import com.remote.client.infrastructure.SocketClient;
import com.remote.client.infrastructure.SocketServer;
import com.remote.client.model.Message;

import java.io.IOException;

public class ServiceMessage {
    private boolean isClientMode;
    private static SocketClient socketClient;
    private static SocketServer socketServer;

    public ServiceMessage() throws IOException {
        try {
            socketServer = SocketServer.getInstance();
            isClientMode = false; // Nếu khởi động server thành công, đây là server
        } catch (IOException e) {
            socketClient = SocketClient.getInstance();
            isClientMode = true; // Nếu không thể khởi động server, đây là client
        }
    }

    public boolean isClientMode() {
        return isClientMode;
    }

    public void connectToServer(String serverIp) {
        if (isClientMode) {
            socketClient = SocketClient.getInstance(serverIp);
            System.out.println("Connected as client to " + serverIp);
        }
    }


    public void sendMessage(String message) {
        try {
            if (isClientMode && socketClient != null) {
                socketClient.sendMessage("CHAT:" + message);
            } else if (!isClientMode && socketServer != null) {
                socketServer.sendMessage("CHAT:" + message);
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public String receiveMessage() {
        try {
            if (isClientMode && socketClient != null) {
                return socketClient.getMessage();
            } else if (!isClientMode && socketClient != null) {
                return socketServer.getMessage();
            }
        } catch (Exception e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
        return null;
    }

    public static void sendMessageClient(Message message) throws IOException {
        socketClient.sendMessage("MSG" + message.getContent());
    }

    public String receiveMessageClient() {
        String message = socketClient.getMessage();
        if (message.startsWith("MSG")){
            return message.substring(3);
        } else {
            return null;
        }
    }

    public void sendMessageServer(Message message) {
        socketServer.sendMessage("MSG" + message.getContent());
    }

    public String receiveMessageServer() {
        String message = socketServer.getMessage();
        if (message.startsWith("MSG")){
            return message.substring(3);
        } else {
            return null;
        }
    }
}

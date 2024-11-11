package com.remote.client.service;

import com.remote.client.infrastructure.SocketClient;

import java.io.IOException;

public class ClientService {
    private SocketClient socketClient;
    private boolean isServerAvailable;

    public ClientService(String serverIP, int serverPort) throws IOException {
        this.socketClient = new SocketClient(serverIP, serverPort);
        this.isServerAvailable = socketClient.isServerAvailable(serverIP, serverPort);
    }

   /* public void startClient() throws IOException {
        if (isServerAvailable) {
            socketClient.sendMessage("Hello from client to server");
            String response = socketClient.receiveMessage();
            System.out.println("Server response: " + response);
        } else {
            startFallbackServer();
        }
    }

    private void startFallbackServer() throws IOException {
        int fallbackPort = 12345;
        System.out.println("Server chính không khả dụng, client đang hoạt động như server tạm thời");

        SocketServer fallbackServer = new SocketServer(fallbackPort);
        fallbackServer.startListening();
    }*/

    public void closeConnection() throws IOException {
        socketClient.close();
    }
}

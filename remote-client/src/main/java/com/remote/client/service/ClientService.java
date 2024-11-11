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

    public void closeConnection() throws IOException {
        socketClient.close();
    }
}

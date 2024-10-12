package com.remote.server.infrastructure;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port: " + port);
    }

    public Socket acceptConnection() throws IOException {
        return serverSocket.accept(); // Chấp nhận kết nối từ client
    }

    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}

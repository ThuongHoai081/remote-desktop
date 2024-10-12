package com.remote.server.service;

import com.remote.server.infrastructure.NetworkManager;
import java.io.IOException;
import java.net.Socket;

public class ServerService {
    private NetworkManager networkManager;

    public ServerService() {
        networkManager = new NetworkManager();
    }

    public void startServer() {
        try {
            networkManager.start(8080); // Sử dụng cổng 8080
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = networkManager.acceptConnection(); // Sử dụng kiểu dữ liệu cụ thể
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start(); // Xử lý từng client trong luồng riêng
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

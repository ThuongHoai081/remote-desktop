package com.remote.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Đọc thông điệp từ client
            String message = in.readLine();
            System.out.println("Received from client: " + message);

            // Gửi phản hồi về client
            String response = "Message received: " + message;
            out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Đóng kết nối với client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.remote.client.service;

import com.remote.client.infrastructure.SocketClient;

import java.io.IOException;
import java.net.Socket;

public class ClientService {
    private SocketClient socketClient;
    private Socket socket;
    private boolean isServerAvailable;

    public ClientService(String serverIP, int serverPort) throws IOException {
        this.socketClient = new SocketClient(serverIP, serverPort);
      //  this.isServerAvailable = socketClient.isServerAvailable(serverIP, serverPort);
    }

    public boolean sendToServer(String message, String... param) {
        try {
            socketClient.sendMessageToServer(message);
            for(int i = 0; i < param.length; i++){
                socketClient.sendMessageToServer(param[i]);
            }

            while (true) {
                String response =  socketClient.receiveMessageToServer();
                System.out.println("Server response: " + response);

                if ("success".equals(response)) {
                    System.out.println(message + "thành công!");
                    return true;
                } else {
                    System.out.println("Phản hồi từ server: " + response);
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void closeConnection() throws IOException {
        socketClient.close();
    }
}

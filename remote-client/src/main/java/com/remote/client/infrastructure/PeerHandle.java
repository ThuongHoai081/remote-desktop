package com.remote.client.infrastructure;

import java.io.*;
import java.net.*;

public class PeerHandle extends Thread {
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public PeerHandle(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            String clientMessage = receiveMessage();
            sendMessage("Server nhận được: " + clientMessage);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    public String receiveMessage() throws IOException {
        return inputStream.readUTF();
    }
}

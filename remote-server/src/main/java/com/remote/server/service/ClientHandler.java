package com.remote.server.service;

import com.remote.server.infrastructure.ConnectionManager;
import com.remote.server.infrastructure.PeerManager;

import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final PeerManager peerManager;
    private final ConnectionManager connectionManager;

//    private ObjectInputStream in;
//    private ObjectOutputStream out;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket, PeerManager peerManager, ConnectionManager connectionManager) {
        this.clientSocket = socket;
        this.peerManager = peerManager;
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());

            while (true) {
                if (clientSocket != null && clientSocket.isConnected()) {
                    String command = in.readUTF();
                    System.out.println("Received request:" + command);
                    switch (command.toUpperCase()) {
                        case "REGISTER":
                            handleRegister(in, out);
                            break;
                        case "LOGIN":
                            handleLogin(in, out);
                            break;
                        case "DISCONNECT":
                            System.out.println("Client requested disconnect.");
                            clientSocket.close();
                            return;
                        default:
                            System.out.println("Unknown command received: " + command);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void handleRegister(DataInputStream inputStream, DataOutputStream outputStream) {
        try {
            String clientIp= inputStream.readUTF();
            String clientEmail= inputStream.readUTF();
            String clientUsername= inputStream.readUTF();
            String clientPassword = inputStream.readUTF();
            boolean checkRegister = peerManager.registerPeer(clientIp, clientEmail, clientUsername, clientPassword);
            System.out.println("Received REGISTER request:");
            System.out.println("Ip: " + clientIp);
            System.out.println("Email: " + clientEmail);
            System.out.println("Password: " + clientPassword);
            if(checkRegister == true) {
                sendResponse(outputStream, "success");
            }else{
                sendResponse(outputStream, "false");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLogin(DataInputStream inputStream, DataOutputStream outputStream) {
        try {
            String clientIp= inputStream.readUTF();
            String clientEmail= inputStream.readUTF();
            String clientPassword = inputStream.readUTF();
            boolean checkLogin = peerManager.loginPeer(clientIp, clientEmail, clientPassword);
            System.out.println("Received Login request:");
            System.out.println("Email: " + clientEmail);
            System.out.println("Password: " + clientPassword);
            if(checkLogin == true){
                sendResponse(outputStream, "success");
            }else{
                sendResponse(outputStream, "false");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(DataOutputStream outputStream, String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.remote.client.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ClientService {
    private Socket socket;
    private ServerSocket peerServerSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String sessionId;

    public void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Gửi yêu cầu kết nối P2P
            out.writeObject("P2P_REQUEST");

            new Thread(this::listenToServer).start();
            startPeerServer();

            // Gọi phương thức xác thực người dùng
            authenticateUser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenToServer() {
        try {
            while (true) {
                Object message = in.readObject();
                if (message instanceof String) {
                    String serverMessage = (String) message;

                    if (serverMessage.startsWith("CONNECT_TO_PEER")) {
                        String peerAddress = serverMessage.split(" ")[1];
                        connectToPeer(peerAddress);
                    } else if (serverMessage.startsWith("AUTH_RESPONSE")) {
                        handleAuthResponse(serverMessage);
                    }else if (serverMessage.startsWith("REGISTER_SUCCESS")) {
                        handleRegisterResponse(serverMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPeerServer() {
        try {
            peerServerSocket = new ServerSocket(0);
            System.out.println("Peer server started on port: " + peerServerSocket.getLocalPort());

            Executors.newSingleThreadExecutor().execute(() -> {
                while (!peerServerSocket.isClosed()) {
                    try {
                        Socket incomingPeer = peerServerSocket.accept();
                        handlePeerConnection(incomingPeer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePeerConnection(Socket peerSocket) {
        try {

            System.out.println("Handling incoming peer connection from " + peerSocket.getInetAddress());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleRegisterResponse(String response) {
        try {

            String[] parts = response.split(" ");
            if (parts.length >= 4 && parts[0].equals("REGISTER_SUCCESS")) {
                String userId = parts[1];
                sessionId = parts[2];

                System.out.println("Registration successful!");
                System.out.println("User ID: " + userId);
                System.out.println("Session ID: " + sessionId);
            } else {
                System.err.println("Invalid REGISTER_SUCCESS message format.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectToPeer(String peerAddress) {
        System.out.println("Connecting to peer at " + peerAddress);
    }

    private void authenticateUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try {
            //out.writeObject("CONNECT_REQUEST " + userId + " " + password);
            out.writeObject("CONNECT_REQUEST " + userId + " " + password + " " + sessionId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleAuthResponse(String response) {
        if (response.contains("Connection successful")) {
            System.out.println("Authentication successful! You can now connect to peers.");
        } else {
            System.out.println("Authentication failed: " + response);
            closeConnection();
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

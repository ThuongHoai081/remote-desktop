package com.remote.server.service;

import com.remote.server.infrastructure.ConnectionManager;
import com.remote.server.infrastructure.PeerManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final PeerManager peerManager;
    private final ConnectionManager connectionManager;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket socket, PeerManager peerManager, ConnectionManager connectionManager) {
        this.clientSocket = socket;
        this.peerManager = peerManager;
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            String message;
            while ((message = in.readObject().toString()) != null) {
                if (message.startsWith("CONNECT_REQUEST")) {
                    processConnectRequest(message);
                } else {
                    out.writeObject("Unknown command: " + message);
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

    private void processConnectRequest(String message) {
        try {
            String[] parts = message.split(" ");
            if (parts.length == 3) {
                String targetId = parts[1];
                String password = parts[2];
                String sessionId = parts[3];

                // Gọi `ConnectionManager` để xác thực và kết nối
                boolean isConnected = connectionManager.connectWithPassword(targetId, password, sessionId);

                if (isConnected) {
                    out.writeObject("AUTH_RESPONSE SUCCESS");
                } else {
                    out.writeObject("AUTH_RESPONSE FAILURE");
                }

            } else {
                out.writeObject("Invalid connect request format. Use: CONNECT_REQUEST <TargetID> <Password>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void ResiterSuccess(String sessionId, String userId) {
        try {
            out.writeObject("REGISTER_SUCCESS " + userId + " " + sessionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendResponse(String message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

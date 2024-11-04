package com.remote.server.service;

import com.remote.server.infrastructure.*;

import java.util.Random;

import java.io.IOException;
import java.net.Socket;

public class ServerService {
    private NetworkManager networkManager;
    private RedisUtils redisUtils;

    private DatabaseManager databaseManager;
    private final PeerManager peerManager;
    private final ConnectionManager connectionManager;

    public ServerService() {
        networkManager = new NetworkManager();
        databaseManager = new DatabaseManager("localhost", 6379);
        peerManager = new PeerManager(databaseManager);
        connectionManager = new ConnectionManager(databaseManager);
        databaseManager.connect();
    }

    public void startServer() {
        try {
            networkManager.start(8080);
            System.out.println("Server is running...");

            while (true) {
                acceptClientConnection();
            }
        } catch (IOException e) {
            System.err.println("Failed to start the server: " + e.getMessage());
        } finally {
            cleanupResources();
        }
    }

    private void acceptClientConnection() {
        try {
            Socket clientSocket = networkManager.acceptConnection();
            handleClientConnection(clientSocket);
        } catch (IOException e) {
            System.err.println("Failed to accept client connection: " + e.getMessage());
        }
    }

    private void handleClientConnection(Socket clientSocket) {
        //String clientInfo = clientSocket.getInetAddress().getHostAddress();
        Random random = new Random();
        int firstOctet = random.nextInt(256);
        int secondOctet = random.nextInt(256);
        int thirdOctet = random.nextInt(256);
        int fourthOctet = random.nextInt(256);

        String clientInfo = firstOctet + "." + secondOctet + "." + thirdOctet + "." + fourthOctet;

        String peerId = peerManager.registerPeer(clientInfo);
        //String password = redisUtils.getPeerInfo(peerId + "_password");
        String sessionId = connectionManager.registerClient(peerId);

        databaseManager.set("client:" + clientInfo, "connected");

        String clientStatus = databaseManager.get("client:" + clientInfo);
        System.out.println("Client status: " + clientStatus + " with Peer ID: " + peerId + " and Session ID: " + sessionId);

        ClientHandler clientHandler = new ClientHandler(clientSocket, peerManager, connectionManager);
        new Thread(clientHandler).start();

        clientHandler.ResiterSuccess(sessionId, peerId);

//        String sessionIdB = "other-session-id";
//         connectPeers(sessionId, sessionIdB, peerId, "inputPassword");
    }
//    public void connectPeers(String sessionIdA, String sessionIdB, String peerId, String inputPassword) {
//        // Xác thực kết nối trước khi tạo kết nối giữa hai phiên
//        if (connectionManager.validateConnection(peerId, inputPassword)) {
//            connectionManager.connectClients(sessionIdA, sessionIdB);
//            System.out.println("Peer connection established between session " + sessionIdA + " and session " + sessionIdB);
//        } else {
//            System.out.println("Authentication failed for peer: " + peerId);
//        }
//    }


    private void cleanupResources() {
        databaseManager.disconnect();
        System.out.println("Disconnected from Redis.");
    }
}

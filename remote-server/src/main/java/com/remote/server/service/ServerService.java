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
    private ClientHandler clientHandler;

    public ServerService() {
        networkManager = new NetworkManager();
        databaseManager = new DatabaseManager("localhost", 6379);
        peerManager = new PeerManager(databaseManager);
        connectionManager = new ConnectionManager(databaseManager);
        databaseManager.connect();
    }

    public void startServer() {
        try {
            networkManager.start(1234);
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

        ClientHandler clientHandler = new ClientHandler(clientSocket, peerManager, connectionManager);
        new Thread(clientHandler).start();

    }

    private void cleanupResources() {
        databaseManager.disconnect();
        System.out.println("Disconnected from Redis.");
    }
}

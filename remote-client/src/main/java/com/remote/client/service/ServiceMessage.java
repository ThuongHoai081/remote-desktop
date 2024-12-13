package com.remote.client.service;

import com.remote.client.infrastructure.ConnectionInitiatorClient;
import com.remote.client.infrastructure.ConnectionInitiatorServer;

import java.io.IOException;

public class ServiceMessage {
    private boolean isClientMode;

    public ConnectionInitiatorClient connectClient;
    public ConnectionInitiatorServer connectServer;

    public ServiceMessage(){
        connectServer = ConnectionInitiatorServer.getInstance();
        if(connectServer == null){
            connectClient = ConnectionInitiatorClient.getInstance();
            isClientMode = true;
        } else{
            isClientMode = false;
        }
    }

    public boolean isClientMode() {
        return isClientMode;
    }

    public void sendMessage(String message) {
        try {
            if (isClientMode && connectClient != null) {
                connectClient.sendChatMessage("CHAT:" + message);
            } else if (!isClientMode && connectServer != null) {
                connectServer.sendChatMessage("CHAT:" + message);
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public String receiveMessage() {
        try {
            if (isClientMode && connectClient != null) {
                String receivedMessage = connectClient.getMessage();
                if (receivedMessage != null && receivedMessage.startsWith("CHAT:")){
                    return receivedMessage.substring(4);
                }
            } else if (!isClientMode && connectServer != null) {
                String receivedMessage = connectServer.getMessage();
                if (receivedMessage != null && receivedMessage.startsWith("CHAT:")){
                    return receivedMessage.substring(4);
                }
            }
        } catch (Exception e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
        return null;
    }
}

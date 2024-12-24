package com.remote.client.service;

import com.remote.client.infrastructure.ConnectionInitiatorClient;
import com.remote.client.infrastructure.ConnectionInitiatorServer;

import java.awt.image.BufferedImage;
import java.io.File;
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
                connectClient.sendChatMessage(message);
            } else if (!isClientMode && connectServer != null) {
                connectServer.sendChatMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public void sendImage(BufferedImage image) {
        try {
            if (isClientMode && connectClient != null) {
                connectClient.sendImageMessage(image);
            } else if (!isClientMode && connectServer != null) {
                connectServer.sendImageMessage(image);
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}

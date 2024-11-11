package com.remote.client.infrastructure;

import java.awt.*;
import java.io.IOException;

public class ConnectionInitiatorServer {
    private static ConnectionInitiatorServer instance = null;
    private SocketServer socket;
    private String serverPassword;
    private Boolean connected = false;

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    private ConnectionInitiatorServer(String serverPassword) {
        System.out.println("password: "+ serverPassword);
        this.serverPassword = serverPassword;
    }

    public static ConnectionInitiatorServer getInstance (String serverPassword) {
        if (instance == null) {
            instance = new ConnectionInitiatorServer(serverPassword);
        }
        return instance;
    }

    public static ConnectionInitiatorServer getInstance () {
        return instance;
    }

    public void initiateConnection() throws IOException {
        socket = SocketServer.getInstance();
        AuthenticatorServer auth = new AuthenticatorServer(socket, serverPassword);
        System.out.println("ServerPassword" + serverPassword);
        while(!auth.isValid()) {
            socket.sendMessage("invalid");
        }
        // auth valid
        socket.sendMessage("valid");
        socket.sendMessage(getWidth());
        socket.sendMessage(getHeight());
        initiateFrameSending();
    }

    void initiateFrameSending() {
        Rectangle rect = new Rectangle(dim);
        new SendFrameServer(rect);
        new ReceiveEventsServer();
    }

    String getWidth() {
        return Double.toString(dim.getWidth());
    }
    String getHeight() {
        return Double.toString(dim.getHeight());
    }

    public Boolean isConnected () {
        return connected;
    }
}

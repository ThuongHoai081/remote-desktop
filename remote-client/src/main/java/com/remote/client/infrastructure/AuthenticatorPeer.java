package com.remote.client.infrastructure;

import java.io.IOException;

public class AuthenticatorPeer {
    private static AuthenticatorPeer instance = null;
    private SocketClient socket;

    private AuthenticatorPeer(SocketClient socket) {
        this.socket = socket;
    }

    public static AuthenticatorPeer getInstance (SocketClient socket) {
        if (instance == null) {
            instance = new AuthenticatorPeer(socket);
        }
        return instance;
    }

    public boolean isValid(String inputPassword) throws IOException {
        sendPassword(inputPassword);
        return "valid".equals(getResult());
    }
    public void sendPassword(String message) throws IOException {
        socket.sendMessage(message);
    }
    public String getResult () {
        return socket.getMessage();
    }
    public void close(){
        instance = null;
    }
}

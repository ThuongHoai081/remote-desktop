package com.remote.client.infrastructure;

import java.io.IOException;

public class AuthenticatorClient {
    private static AuthenticatorClient instance = null;
    private SocketClient socket;

    private AuthenticatorClient (SocketClient socket) {
        this.socket = socket;
    }

    public static AuthenticatorClient getInstance (SocketClient socket) {
        if (instance == null) {
            instance = new AuthenticatorClient(socket);
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

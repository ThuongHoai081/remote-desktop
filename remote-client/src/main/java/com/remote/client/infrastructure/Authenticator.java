package com.remote.client.infrastructure;

public class Authenticator {
    private SocketServer socket;
    private String ServerPassword;

    public Authenticator(SocketServer socket, String ServerPassword) {
        this.ServerPassword = ServerPassword;
        this.socket = socket;
    }

    public Boolean isValid () {
        String inputPassword = socket.getMessage();
        return ServerPassword.equals(inputPassword);
    }
}

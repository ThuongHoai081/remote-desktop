package com.remote.client.infrastructure;

public class AuthenticatorServer {
    private SocketServer socket;
    private String ServerPassword;

    public AuthenticatorServer(SocketServer socket, String ServerPassword) {
        this.ServerPassword = ServerPassword;
        this.socket = socket;
    }

    public Boolean isValid () {
        String inputPassword = socket.getMessage();
        return ServerPassword.equals(inputPassword);
    }
}

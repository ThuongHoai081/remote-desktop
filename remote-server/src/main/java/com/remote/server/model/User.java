package com.remote.server.model;

import java.util.UUID;

public class User {
    private String clientId;
    private String password;

    public User(String clientId, String password) {
        this.clientId = clientId;
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getPassword() {
        return password;
    }
}

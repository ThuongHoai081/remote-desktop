package com.remote.server.model;

import java.util.UUID;

public class Session {
    private String sessionId;
    private String clientId;

    public Session(String clientId) {
        this.sessionId = UUID.randomUUID().toString();
        this.clientId = clientId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientId() {
        return clientId;
    }

    public void notifyConnectionReady(String otherSessionId) {
        System.out.println("Client " + clientId + " được thông báo về kết nối với client " + otherSessionId);
    }
}

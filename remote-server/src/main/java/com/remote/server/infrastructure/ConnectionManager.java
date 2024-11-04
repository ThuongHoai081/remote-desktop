package com.remote.server.infrastructure;

import com.remote.server.model.Session;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private RedisUtils redisUtils;

    public ConnectionManager(DatabaseManager databaseManager) {
        this.redisUtils = databaseManager.getRedisUtils();
        if (this.redisUtils == null) {
            throw new IllegalStateException("RedisUtils is not initialized.");
        }
    }
    private Map<String, Session> activeSessions = new ConcurrentHashMap<>();

    public String registerClient(String clientId) {
        String sessionId = UUID.randomUUID().toString();
        activeSessions.put(sessionId, new Session(clientId));
        return sessionId;
    }

    public void connectClients(String sessionIdA, String sessionIdB) {
        Session sessionA = activeSessions.get(sessionIdA);
        Session sessionB = activeSessions.get(sessionIdB);

        if (sessionA != null && sessionB != null) {
            sessionA.notifyConnectionReady(sessionIdB);
            sessionB.notifyConnectionReady(sessionIdA);
        } else {
            System.out.println("Một trong các phiên không hợp lệ.");
        }
    }
    public boolean validateConnection(String peerId, String inputPassword) {
        String storedPassword = redisUtils.getPeerInfo(peerId + "_password");
        if (storedPassword != null && storedPassword.equals(inputPassword)) {
            System.out.println("Mật khẩu hợp lệ. Đang kết nối với peer " + peerId);
            return true;
        } else {
            System.out.println("ID hoặc mật khẩu không hợp lệ.");
            return false;
        }
    }

    public boolean connectWithPassword(String targetId, String inputPassword, String sessionId) {
        String storedPassword = redisUtils.getPeerInfo(targetId + "_password");
        if (storedPassword != null && storedPassword.equals(inputPassword)) {
            String sessionIdA = sessionId;
            String sessionIdB = getSessionIdForClient(targetId);
            if (sessionIdA != null && sessionIdB != null && !sessionIdA.equals(sessionIdB)) {
                connectClients(sessionIdA, sessionIdB);
                return true;
            }
        }
        return false;
    }


    private String getSessionIdForClient(String clientId) {
        for (Map.Entry<String, Session> entry : activeSessions.entrySet()) {
            if (entry.getValue().getClientId().equals(clientId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getSessionId(String peerId) {
        for (Map.Entry<String, Session> entry : activeSessions.entrySet()) {
            if (entry.getValue().getClientId().equals(peerId)) {
                return entry.getKey();
            }
        }
        return null;
    }

}

package com.remote.server.service;

import com.remote.server.model.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void addSession(Session session) {
        sessions.put(session.getSessionId(), session);
    }

    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}

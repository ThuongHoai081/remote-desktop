package com.remote.server.infrastructure;

public class DatabaseManager {
    private RedisUtils redisUtils;

    public DatabaseManager(String redisHost, int redisPort) {
        this.redisUtils = new RedisUtils(redisHost, redisPort);
    }

    public RedisUtils getRedisUtils() {
        return redisUtils;
    }

    public void connect() {
        if (!redisUtils.isConnected()) {
            redisUtils.set("testKey", "testValue");
            System.out.println("Connected to Redis.");
        }
    }

    public void disconnect() {
        if (redisUtils != null) {
            redisUtils.close();
            System.out.println("Disconnected from Redis.");
        }
    }

    public void set(String key, String value) {
        redisUtils.set(key, value);
    }

    public String get(String key) {
        return redisUtils.get(key);
    }

    public void delete(String key) {
        redisUtils.delete(key);
    }

    public boolean exists(String key) {
        return redisUtils.get(key) != null;
    }
}

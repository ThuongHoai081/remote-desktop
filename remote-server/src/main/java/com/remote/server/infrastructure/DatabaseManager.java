package com.remote.server.infrastructure;

public class DatabaseManager {
    private RedisUtils redisUtils;

    // Khởi tạo DatabaseManager với địa chỉ Redis
    public DatabaseManager(String redisHost, int redisPort) {
        this.redisUtils = new RedisUtils(redisHost, redisPort);
    }

    // Lấy RedisUtils để sử dụng
    public RedisUtils getRedisUtils() {
        return redisUtils;
    }

    // Kết nối đến Redis
    public void connect() {
        if (!redisUtils.isConnected()) {
            redisUtils.set("testKey", "testValue"); // Kiểm tra kết nối bằng cách gửi một lệnh đơn giản
            System.out.println("Connected to Redis.");
        }
    }

    // Ngắt kết nối đến Redis
    public void disconnect() {
        if (redisUtils != null) {
            redisUtils.close();
            System.out.println("Disconnected from Redis.");
        }
    }

    // Các phương thức để thao tác với Redis thông qua RedisUtils
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
        return redisUtils.get(key) != null; // Kiểm tra nếu giá trị tồn tại
    }
}

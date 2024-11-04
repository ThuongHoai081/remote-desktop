package com.remote.server.infrastructure;

import redis.clients.jedis.Jedis;
import java.util.Set;

public class RedisUtils {
    private Jedis jedis;

    // Khởi tạo RedisUtils với địa chỉ Redis
    public RedisUtils(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    // Lưu giá trị vào Redis
    public void set(String key, String value) {
        jedis.set(key, value);
    }

    // Lấy giá trị từ Redis
    public String get(String key) {
        return jedis.get(key);
    }

    // Xóa một key khỏi Redis
    public void delete(String key) {
        jedis.del(key);
    }

    // Kiểm tra kết nối đến Redis
    public boolean isConnected() {
        return "PONG".equals(jedis.ping());
    }

    // Đóng kết nối
    public void close() {
        jedis.close();
    }

    // Thêm thông tin peer vào Redis
    public void setPeerInfo(String key, String value) {
        set(key, value);
    }

    // Lấy thông tin peer từ Redis
    public String getPeerInfo(String key) {
        return get(key);
    }

    // Xóa thông tin của một peer
    public void removePeer(String key) {
        delete(key);
    }

    // Thêm peer vào tập hợp các peers hoạt động
    public void addToSet(String setName, String value) {
        jedis.sadd(setName, value);
    }

    // Xóa peer khỏi tập hợp các peers hoạt động
    public void removeFromSet(String setName, String value) {
        jedis.srem(setName, value);
    }

    // Lấy tất cả các peer đang hoạt động
    public Set<String> getActivePeers() {
        return jedis.smembers("activePeers");
    }
}

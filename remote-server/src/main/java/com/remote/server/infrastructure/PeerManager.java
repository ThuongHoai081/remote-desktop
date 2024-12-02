package com.remote.server.infrastructure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remote.server.model.PeerInfo;

import java.util.Random;

public class PeerManager {
    private RedisUtils redisUtils;

    public PeerManager(DatabaseManager databaseManager) {
        this.redisUtils = databaseManager.getRedisUtils();
        if (this.redisUtils == null) {
            throw new IllegalStateException("RedisUtils is not initialized.");
        }
    }

    public boolean registerPeer(String ip, String email, String username, String password) {
        if (email == null || email.isEmpty() || username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Thông tin không được để trống.");
            return false;
        }

        if (redisUtils.isMemberOfSet("activePeers", ip)) {
            System.out.println("Peer với IP " + ip + " đã tồn tại.");
            return false;
        }
        PeerInfo peerInfo = new PeerInfo(email, username, password);
        redisUtils.hset(ip, "email", peerInfo.getEmail());
        redisUtils.hset(ip, "username", peerInfo.getUsername());
        redisUtils.hset(ip, "password", peerInfo.getPassword());
        redisUtils.addToSet("activePeers", ip);

        System.out.println("Peer mới đăng ký: email: " + email + ", Mật khẩu: " + password);
        return true;
    }

    public boolean loginPeer(String ip, String email, String password) {
        String storedEmail = redisUtils.hget(ip, "email");
        String storedPassword = redisUtils.hget(ip, "password");

        if (storedEmail == null || storedPassword == null) {
            System.out.println("Thông tin đăng nhập không tồn tại cho Peer: " + ip);
            return false;
        }

        if (storedEmail.equals(email) && storedPassword.equals(password)) {
            System.out.println("Đăng nhập thành công cho Peer: " + ip);
            return true;
        } else {
            System.out.println("Thông tin đăng nhập sai cho Peer: " + ip);
            return false;
        }
    }


}


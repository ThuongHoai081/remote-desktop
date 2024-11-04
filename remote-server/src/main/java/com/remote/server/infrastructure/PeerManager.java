package com.remote.server.infrastructure;

import com.remote.server.model.User;

import java.util.UUID;
import java.util.Random;

public class PeerManager {
    private RedisUtils redisUtils;

    public PeerManager(DatabaseManager databaseManager) {
        this.redisUtils = databaseManager.getRedisUtils();
        if (this.redisUtils == null) {
            throw new IllegalStateException("RedisUtils is not initialized.");
        }
    }

    public String registerPeer(String connectionDetails) {
        String peerId = redisUtils.getPeerInfo(connectionDetails + "_id");

        if (peerId == null) {
            peerId = UUID.randomUUID().toString();
            String password = generateRandomPassword(6);

            redisUtils.setPeerInfo(connectionDetails + "_id", peerId);
            redisUtils.setPeerInfo(peerId + "_details", connectionDetails);
            redisUtils.setPeerInfo(peerId + "_password", password);
            redisUtils.addToSet("activePeers", peerId);
            System.out.println("Peer ID mới: " + peerId + ", IP: " + connectionDetails + ", Mật khẩu: " + password);
        } else {
            String password = redisUtils.getPeerInfo(peerId + "_password");
            System.out.println("Peer ID cố định: " + peerId + ", IP: " + connectionDetails + ", Mật khẩu: " + password);
        }

        return peerId;
    }


    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public void unregisterPeer(String peerId) {
        redisUtils.removePeer(peerId + "_details");
        redisUtils.removePeer(peerId + "_password");
        redisUtils.removeFromSet("activePeers", peerId);
        System.out.println("Peer " + peerId + " đã hủy đăng ký.");
    }

    public void establishP2PConnection(String clientAId, String clientBId) {
        System.out.println("Establishing P2P connection between " + clientAId + " and " + clientBId);
    }

    public void closeP2PConnection(String clientAId, String clientBId) {
        System.out.println("Closing P2P connection between " + clientAId + " and " + clientBId);
    }
}


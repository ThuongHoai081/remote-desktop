package com.remote.server.infrastructure;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import java.util.Set;

public class RedisUtils {
    private Jedis jedis;

    public RedisUtils(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public String get(String key) {
        return jedis.get(key);
    }
    public void hset(String key, String field, String value) {
        jedis.hset(key, field, value);
    }

    public String hget(String key, String field) {
        return jedis.hget(key, field);
    }


    public void delete(String key) {
        jedis.del(key);
    }

    public boolean isConnected() {
        return "PONG".equals(jedis.ping());
    }

    public void close() {
        jedis.close();
    }

    public void setPeerInfo(String key, String value) {
        set(key, value);
    }

    public String getPeerInfo(String key) {
        return get(key);
    }

    public void removePeer(String key) {
        delete(key);
    }

    public void addToSet(String setName, String value) {
        jedis.sadd(setName, value);
    }

    public void removeFromSet(String setName, String value) {
        jedis.srem(setName, value);
    }

    public Set<String> getActivePeers() {
        return jedis.smembers("activePeers");
    }
    public boolean isMemberOfSet(String setName, String value) {
        return jedis.sismember(setName, value);
    }
}

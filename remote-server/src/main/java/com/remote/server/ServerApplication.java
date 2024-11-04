package com.remote.server;

import com.remote.server.service.ServerService;

/**
 * Hello world!
 *
 */
public class ServerApplication
{
    public static void main(String[] args) {
        ServerService serverService = new ServerService();
        serverService.startServer();
    }
}

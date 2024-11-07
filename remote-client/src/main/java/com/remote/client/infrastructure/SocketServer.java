package com.remote.client.infrastructure;

import java.io.*;
import java.net.*;

public class SocketServer {
    private ServerSocket serverSocket;

    public SocketServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void startListening() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            new PeerHandle(socket).start();
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}

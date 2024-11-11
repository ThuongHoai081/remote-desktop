package com.remote.client.infrastructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

public class SocketServer {
    private static SocketServer instance = null;

    private int port = 4907;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public SocketServer() throws IOException {
        System.out.println("Waiting for Accept");
        socket = new ServerSocket(port).accept();
        System.out.println("Accept finished");
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public static SocketServer getInstance () throws IOException {
        if (instance == null) {
            instance = new SocketServer();
        }
        return instance;
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
        }
    }

    public void sendImage(BufferedImage image) {
        try {
            ImageIO.write(image, "jpeg", socket.getOutputStream());
        } catch (IOException e) {

        }
    }

    public String getMessage() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            return null;
        }
    }

    /*public void startListening() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            new PeerHandle(socket).start();
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }*/
}

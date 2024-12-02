package com.remote.client.infrastructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

public class SocketServer implements Closeable {
    private static SocketServer instance = null;

    private int port = 4907;
    private ServerSocket serverSocket;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private PrintWriter out;
    private BufferedReader in;

    public SocketServer() throws IOException {
        System.out.println("Waiting for Accept");
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        System.out.println("Accept finished");
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
       // out = new PrintWriter(socket.getOutputStream(), true);
       // in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
    @Override
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            instance = null;
        }
    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public String receiveMessageToServer() throws IOException {
        return in.readLine();
    }
}

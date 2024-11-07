package com.remote.client.infrastructure;

import java.io.*;
import java.net.Socket;

public class SocketClient implements Closeable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SocketClient()  {

    }

    public SocketClient(String host, int port) throws IOException {
        connect(host, port);
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    @Override
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean isServerAvailable(String serverIP, int serverPort) {
        try {
            Socket testSocket = new Socket(serverIP, serverPort);
            testSocket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

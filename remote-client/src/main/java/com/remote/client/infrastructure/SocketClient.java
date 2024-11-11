package com.remote.client.infrastructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class SocketClient implements Closeable {

    private static SocketClient instance = null;

    public static boolean correctIP = false;

    private Integer port = 4907;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public SocketClient(String serverIp)  {
        try {
            socket = new Socket(serverIp, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            correctIP = true;
        } catch (IOException e) {
            System.out.println("Failed connection.");
        }
    }

    public static SocketClient getInstance (String serverIp) {
        if (instance == null) {
            instance = new SocketClient(serverIp);
        }
        return instance;
    }

    public static SocketClient getInstance() {
        return instance;
    }

    public void sendMessage(String message) throws  IOException {
            outputStream.writeUTF(message);
    }

    public String getMessage() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            return null;
        }
    }

    public BufferedImage getImage() {
        try {
            byte[] bytes = new byte[1024 * 1024];
            int count = 0;
            do {
                count+= socket.getInputStream().read(bytes, count, bytes.length - count);

            } while(!(count > 4 && bytes[count - 2] == (byte) -1 && bytes[count - 1] == (byte) -39));

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));

            return image;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
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

    public void sendMessageToServer(String message) throws IOException {
        out.println(message);
    }

    public String receiveMessageToServer() throws IOException {
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

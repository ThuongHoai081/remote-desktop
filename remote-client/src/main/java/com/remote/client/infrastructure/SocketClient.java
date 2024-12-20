package com.remote.client.infrastructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient implements Closeable {

    private static SocketClient instance = null;

    public static boolean correctIP = false;

    private Integer port = 4907;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private static SocketClient chatInstance = null;
    private static SocketClient streamingInstance = null;
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

    public FilterInputStream getInputStream() {
        return inputStream;
    }

    public FilterOutputStream getOutputStream() {
        return outputStream;
    }

    // Gửi hình ảnh qua kết nối
    public void sendImage(BufferedImage image) {
        try {
            ImageIO.write(image, "jpeg", socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error sending image: " + e.getMessage());
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

    public static SocketClient getStreamingInstance(String serverIp, int port) {
        if (streamingInstance == null) {
            try {
                streamingInstance = new SocketClient(serverIp, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return streamingInstance;
    }

    public static SocketClient getChatInstance(String serverIp, int port) {
        if (chatInstance == null) {
            try {
                chatInstance = new SocketClient(serverIp, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return chatInstance;
    }


    public SocketClient(String host, int port) throws IOException {
        connect(host, port);
    }

    public void sendFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            byte[] fileNameBytes = file.getName().getBytes(StandardCharsets.UTF_8);
            dos.writeInt(fileNameBytes.length);
            dos.write(fileNameBytes);
            dos.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            dos.flush();
        }
    }

    public File receiveFile() throws IOException {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            int fileNameLength = dis.readInt();
            byte[] fileNameBytes = new byte[fileNameLength];
            dis.readFully(fileNameBytes);
            String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

            long fileSize = dis.readLong();
            File file = new File("C:\\Users\\HP\\Downloads", fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                long totalBytesRead = 0;
                int bytesRead;
                while (totalBytesRead < fileSize) {
                    bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead));
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
            }
            return file;
        }
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public String receiveMessageToServer() throws IOException {
        return in.readLine();
    }

    public BufferedImage getImageMessage() {
        try {
            byte[] bytes = new byte[1024 * 1024];
            int count = 0;
            do {
                count+= chatInstance.getInputStream().read(bytes, count, bytes.length - count);

            } while(!(count > 4 && bytes[count - 2] == (byte) -1 && bytes[count - 1] == (byte) -39));

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));

            return image;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    public void sendImageMessage(BufferedImage image) {
        try {
            ImageIO.write(image, "jpeg", chatInstance.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error sending image: " + e.getMessage());
        }
    }
}

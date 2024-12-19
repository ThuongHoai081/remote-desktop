package com.remote.client.infrastructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SocketServer implements Closeable {
    private static SocketServer instance = null;
    private static SocketServer chatInstance = null;
    private static SocketServer streamingInstance = null;


    private int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    // Khởi tạo kết nối server
    public SocketServer() throws IOException {
        this(4907); // Mặc định sử dụng cổng 4907
    }

    // Khởi tạo kết nối server với cổng cụ thể
    public SocketServer(int port) throws IOException {
        this.port = port;
        System.out.println("Waiting for Accept on port " + port);
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        System.out.println("Accept finished on port " + port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    // Lấy instance của SocketServer cho cổng chính
    public static SocketServer getInstance() throws IOException {
        if (instance == null) {
            instance = new SocketServer(); // Sử dụng cổng mặc định
        }
        return instance;
    }

    // Lấy instance của SocketServer cho cổng chat
    public static SocketServer getChatInstance(int port) {
        if (chatInstance == null) {
            try {
                chatInstance = new SocketServer(port); // Truyền cổng cho kết nối chat
            } catch (IOException e) {
                throw new RuntimeException("Error creating chat socket: " + e.getMessage(), e);
            }
        }
        return chatInstance;
    }

    public static SocketServer getStreamingInstance(int port) {
        if (streamingInstance == null) {
            try {
                streamingInstance = new SocketServer(port);
            } catch (IOException e) {
                throw new RuntimeException("Error creating chat socket: " + e.getMessage(), e);
            }
        }
        return streamingInstance;
    }
    // Gửi thông điệp qua kết nối
    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
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

    // Nhận thông điệp từ kết nối
    public String getMessage() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            System.err.println("Error receiving message: " + e.getMessage());
            return null;
        }
    }

    // Đóng kết nối
    @Override
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            instance = null;
            chatInstance = null;
            System.out.println("SocketServer closed");
        }
    }

    // Đảm bảo cổng chat được mở
    public void closeChatSocket() throws IOException {
        if (chatInstance != null) {
            chatInstance.close();
            chatInstance = null;
        }
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

    public FilterInputStream getInputStream() {
        return inputStream;
    }

    public FilterOutputStream getOutputStream() {
        return outputStream;
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

}

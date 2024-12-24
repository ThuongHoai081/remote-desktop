package com.remote.client.infrastructure;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SocketServer implements Closeable {
    private static SocketServer instance = null;
    private static SocketServer chatInstance = null;
    private static SocketServer streamingInstance = null;
    private SocketServer streamingSocket;
    private SourceDataLine speakers;
    private TargetDataLine microphone = null;

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
    public void sendType(Integer n) {
        try {
            outputStream.writeInt(n);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    public int getType() {
        try {
            return inputStream.readInt();
        } catch (IOException e) {
            return -1;
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

//    public void sendImageMessage(BufferedImage image) {
//        try {
//            ImageIO.write(image, "png", socket.getOutputStream());
//        } catch (IOException e) {
//            System.err.println("Error sending image: " + e.getMessage());
//        }
//    }
public void sendImageMessage(BufferedImage image) {
    try {
        // Chuyển ảnh thành byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Gửi kích thước dữ liệu
        outputStream.writeInt(imageBytes.length);
        outputStream.flush();

        // Gửi dữ liệu ảnh
        outputStream.write(imageBytes);
        outputStream.flush();

        System.out.println("Image sent successfully.");
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

    public BufferedImage getImageMessage() {
        try {
            // Đọc kích thước dữ liệu
            int length = inputStream.readInt();
            if (length <= 0) {
                System.err.println("Error: Invalid image size.");
                return null;
            }

            // Nhận dữ liệu ảnh
            byte[] imageBytes = new byte[length];
            int bytesRead = 0;
            while (bytesRead < length) {
                int result = inputStream.read(imageBytes, bytesRead, length - bytesRead);
                if (result == -1) {
                    throw new IOException("Error: Incomplete image data received.");
                }
                bytesRead += result;
            }

            // Chuyển đổi mảng byte thành ảnh
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bais);

            if (image == null) {
                System.err.println("Error: Unsupported image format.");
                return null;
            }

            System.out.println("Image received successfully.");
            return image;
        } catch (IOException e) {
            System.err.println("Error receiving image: " + e.getMessage());
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
    public OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}

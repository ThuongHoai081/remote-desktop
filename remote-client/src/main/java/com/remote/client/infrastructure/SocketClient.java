package com.remote.client.infrastructure;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
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
    private static SocketClient chatInstance = null;
    private static SocketClient streamingInstance = null;
    private SocketServer streamingSocket;
    private SourceDataLine speakers;
    private TargetDataLine microphone = null;

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
    public void sendType(Integer n) throws  IOException {
        outputStream.writeInt(n);
    }

    public String getMessage() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            return null;
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



    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public String receiveMessageToServer() throws IOException {
        return in.readLine();
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


    public void sendImageMessage(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // Gửi kích thước dữ liệu
            outputStream.writeInt(imageBytes.length);

            // Gửi dữ liệu ảnh
            outputStream.write(imageBytes);
            outputStream.flush();

            System.out.println("Image sent successfully.");
        } catch (IOException e) {
            System.err.println("Error sending image: " + e.getMessage());
        }
    }

    public OutputStream getOutPutStream() {
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
    @Override
    public void close() throws IOException {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } finally {
            instance = null;
            chatInstance = null;
            streamingInstance = null;
            System.out.println("SocketClient on port " + port + " closed.");
        }
    }
}

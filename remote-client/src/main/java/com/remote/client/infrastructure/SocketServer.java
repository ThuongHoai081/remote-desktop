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

    public static SocketServer getStreamingInstance() throws IOException {
        return streamingInstance;
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

    public void sendImageMessage(BufferedImage image) {
        try {
            ImageIO.write(image, "png", socket.getOutputStream());
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
            byte[] bytes = new byte[1024 * 1024]; // 1 MB buffer
            int count = 0;
            int bytesRead;
            boolean isJPEGComplete = false;

            while ((bytesRead = socket.getInputStream().read(bytes, count, bytes.length - count)) != -1) {
                count += bytesRead;

                // Check for JPEG end marker (0xFFD9)
                if (count > 4 && bytes[count - 2] == (byte) 0xFF && bytes[count - 1] == (byte) 0xD9) {
                    isJPEGComplete = true;
                    break;
                }

                // Check for buffer overflow
                if (count >= bytes.length) {
                    System.err.println("Error: Image size exceeds buffer limit.");
                    return null;
                }
            }

            if (!isJPEGComplete) {
                System.err.println("Error: Incomplete image data received.");
                return null;
            }

            // Read the image from the received bytes
            return ImageIO.read(new ByteArrayInputStream(bytes, 0, count));
        } catch (IOException e) {
            System.err.println("Error receiving image: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
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
    public void voiceChat(){
        try{
            InputStream in = socket.getInputStream();
            //audioformat
            AudioFormat format = new AudioFormat(16000, 8, 2, true, true);
            //audioformat
            //selecting and strating speakers
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();

            //for sending
            OutputStream out = null;
            out = socket.getOutputStream();

            //selecting and starting microphone
            microphone = AudioSystem.getTargetDataLine(format);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();


            byte[] bufferForOutput = new byte[1024];
            int bufferVariableForOutput = 0;

            byte[] bufferForInput = new byte[1024];
            int bufferVariableForInput;

            while((bufferVariableForInput = in.read(bufferForInput)) > 0  || (bufferVariableForOutput=microphone.read(bufferForOutput, 0, 1024)) > 0) {
                out.write(bufferForOutput, 0, bufferVariableForOutput);
                speakers.write(bufferForInput, 0, bufferVariableForInput);

            }
        }
        catch(IOException | LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }
}

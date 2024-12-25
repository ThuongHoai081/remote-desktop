package com.remote.client.infrastructure;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public class ReceiveFrameClient extends Thread {

    private ImageView imageView;
    private volatile boolean running = true;

    public ReceiveFrameClient(ImageView imageView) {
        System.out.println("ReceiveFrame");
        this.imageView = imageView;
        start();
    }

    public void run() {
        while (running) {
            try {
                BufferedImage image = SocketClient.getInstance().getImage();
                if (image != null) {
                    WritableImage imageFx = SwingFXUtils.toFXImage(image, null);
                    this.imageView.setImage(imageFx);

                } else {
                    System.err.println("Received null image.");
                }
            } catch (Exception ex) {
                System.err.println("Error while receiving or processing image: " + ex.getMessage());
                try {
                    Thread.sleep(100); // Chờ 100ms trước khi thử lại
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Khôi phục trạng thái ngắt của luồng
                    break; // Thoát vòng lặp nếu luồng bị ngắt
                }
            }
        }
    }
    public void stopReceiveFrameClient() {
        running = false; // Đặt cờ để thoát vòng lặp
        this.interrupt(); // Ngắt luồng nếu đang chờ
    }
}

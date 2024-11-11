package com.remote.client.infrastructure;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public class ReceiveFrameClient extends Thread {

    private ImageView imageView;

    public ReceiveFrameClient(ImageView imageView) {
        System.out.println("ReceiveFrame");

        this.imageView = imageView;
        start();
    }

    public void run() {

        try {
            while (true) {
                BufferedImage image = SocketClient.getInstance().getImage();
                WritableImage imageFx = SwingFXUtils.toFXImage(image, null);
                this.imageView.setImage(imageFx);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

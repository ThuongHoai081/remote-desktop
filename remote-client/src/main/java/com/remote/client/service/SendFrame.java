package com.remote.client.service;

import com.remote.client.infrastructure.SocketServer;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class SendFrame extends Thread {

    Robot robot = null;
    Rectangle rect;
    private volatile boolean running = true;
    public SendFrame(Rectangle rect) {
        System.out.println("SendFrame");

        this.rect = rect;
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
        try {
            robot = new Robot(gDev);
        } catch(Exception e) {
            System.out.println(e);
        }
        start();
    }

    public void run(){
        while(running){
            BufferedImage image = robot.createScreenCapture(rect);
            try {
                SocketServer.getInstance().sendImage(image);
            } catch(Exception ex){
                ex.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    public void stopSendFrameServer() {
        running = false; // Đặt cờ để thoát vòng lặp
        this.interrupt(); // Ngắt luồng nếu đang chờ
    }
}

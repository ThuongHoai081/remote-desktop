package com.remote.client.infrastructure;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class SendFrameServer extends Thread {

    Robot robot = null;
    Rectangle rect;

    public SendFrameServer(Rectangle rect) {
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
        while(true){
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
}

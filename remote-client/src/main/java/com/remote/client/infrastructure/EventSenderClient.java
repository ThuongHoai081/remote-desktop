package com.remote.client.infrastructure;

import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class EventSenderClient {

    private SocketClient socket;
    private ImageView panel;
    private double widthServer, heightServer, widthClient, heightClient;
    private boolean isSendingEvents = true;

    EventSenderClient(ImageView panel, double widthServer, double heightServer) {
        System.out.println("EventSender");
        this.socket = SocketClient.getInstance();
        this.panel = panel;
        this.widthServer = widthServer;
        this.heightServer = heightServer;
        this.widthClient = panel.getFitWidth();
        this.heightClient = panel.getFitHeight();

        panel.setFocusTraversable(true);

        mouseMoved();
        mouseReleased();
        mousePressed();
        keyPressed();
        keyReleased();
    }

    private void mousePressed()  {
        panel.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isSendingEvents) {
                try {
                socket.sendMessage("MP");
                socket.sendMessage(Integer.toString(event.getButton().ordinal()));
                System.out.println("Client mouse pressed" + Integer.toString(event.getButton().ordinal()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}
        });
    }

    private void mouseReleased() {
        panel.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isSendingEvents) {
                try {
                    socket.sendMessage("MR");
                    socket.sendMessage(Integer.toString(event.getButton().ordinal()));
                    System.out.println("Client mouse released" + Integer.toString(event.getButton().ordinal()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private void mouseMoved() {
        panel.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (isSendingEvents) {
                double newX = (double)widthServer / widthClient * event.getX();
                double newY = (double)heightServer / heightClient * event.getY();
                try {
                    socket.sendMessage("MV");
                    socket.sendMessage(Double.toString(newX));
                    socket.sendMessage(Double.toString(newY));
                    System.out.println("Client mouse move");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void keyPressed() {
        panel.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (isSendingEvents) {
                try {
                    socket.sendMessage("KP");
                    socket.sendMessage(event.getCode().getName());
                    System.out.println("Client key pressed");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void keyReleased() {
        panel.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (isSendingEvents) {
                try {
                    socket.sendMessage("KR");
                    socket.sendMessage(event.getCode().getName());
                    System.out.println("Client key released");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }
    public void stop() {
        isSendingEvents = false;
        System.out.println("Stopped event sending.");
    }

    // Phương thức để khôi phục việc gửi sự kiện
    public void start() {
        isSendingEvents = true;
        System.out.println("Resumed event sending.");
    }
}

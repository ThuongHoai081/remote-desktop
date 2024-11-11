package com.remote.client.infrastructure;

import javafx.scene.image.ImageView;

import java.io.IOException;

public class ConnectionInitiatorClient {
    private static ConnectionInitiatorClient instance;
    private SocketClient socket;
    private String clientPassword;

    private ConnectionInitiatorClient(String serverIp) throws IOException {

        socket = SocketClient.getInstance(serverIp);
    }

    public static ConnectionInitiatorClient getInstance (String serverIp) throws IOException {
        if (instance == null) {
            instance = new ConnectionInitiatorClient(serverIp);
        }
        return instance;
    }

    public static ConnectionInitiatorClient getInstance () {
        return instance;
    }

    public Boolean checkPassword(String password) throws IOException {
        AuthenticatorClient auth = AuthenticatorClient.getInstance(socket);
        return auth.isValid(password);
    }

    public void initializeStreaming(ImageView imageView) {
        String width = socket.getMessage();
        String height = socket.getMessage();
        System.out.println("Width " + width);
        System.out.println("Height " + height);
        new ReceiveFrameClient(imageView);
        new EventSenderClient(imageView, Double.parseDouble(width), Double.parseDouble(height));
    }
}

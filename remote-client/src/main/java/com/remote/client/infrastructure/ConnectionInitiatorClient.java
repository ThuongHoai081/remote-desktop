package com.remote.client.infrastructure;

import com.remote.client.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ConnectionInitiatorClient {
    private static ConnectionInitiatorClient instance;
    private SocketClient socket;
    private String clientPassword;

    private SocketClient chatSocket;
    private SocketClient streamingSocket;
    private VoiceChatClient voiceChatClient;
    private ReceiveFrame receiveFrameClient;
    private EventSender eventSenderClient;

    private ReceiveMessageClient receiveMessageClient;
    private AuthenticatorPeer auth;

    private boolean isConnected = true;

    private ConnectionInitiatorClient(String serverIp) {
        socket = SocketClient.getInstance(serverIp);
        chatSocket = SocketClient.getChatInstance(serverIp, 5000); // Port for chat
        streamingSocket = SocketClient.getStreamingInstance(serverIp, 6000);
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
        auth = AuthenticatorPeer.getInstance(socket);
        return auth.isValid(password);
    }

    public void sendImageMessage(BufferedImage image) throws IOException {
        if (chatSocket != null) {
            chatSocket.sendType(1);
            chatSocket.sendImageMessage(image);
        }
    }

    public void sendChatMessage(String message) throws IOException {
        // Chỉ sử dụng socket chat khi đang ở chế độ Client
        if (chatSocket != null) {
            chatSocket.sendType(0);
            chatSocket.sendMessage(message);  // Gửi qua socket chat
        }
    }

    public void initializeStreaming(ImageView imageView) {
        String width = socket.getMessage();
        String height = socket.getMessage();
       // receiveFrameClient = new ReceiveFrameClient(imageView);
      //  new EventSenderClient(imageView, Double.parseDouble(width), Double.parseDouble(height));
        if (isConnected) {
            receiveFrameClient = new ReceiveFrame(imageView);
            eventSenderClient = new EventSender(imageView, Double.parseDouble(width), Double.parseDouble(height));
        }

        Platform.runLater(() -> {
            try {
                Parent messagePane = FXMLLoader.load(HelloApplication.class.getResource("MessageView.fxml"));
                Stage messageStage = new Stage();
                Scene messageScene = new Scene(messagePane);
                messageStage.setScene(messageScene);
                messageStage.setTitle("Messages");
                messageStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initializeMessage(VBox messageContainer) {
        if (isConnected) {
            receiveMessageClient = new ReceiveMessageClient(chatSocket, messageContainer);
        }
    }

    public void initializeStreaming() {
        if (isConnected) {
            voiceChatClient = new VoiceChatClient(streamingSocket);
        }
    }

    public void cancelStreaming(){
        if (isConnected && voiceChatClient != null) {
            voiceChatClient.cleanUp();
        }
    }
    public void cancelConnect() {
        try {
            // Ngừng tất cả các tiến trình gửi nhận sự kiện
            if (eventSenderClient != null) {
                eventSenderClient.stop();
                eventSenderClient = null;
            }
            if (receiveFrameClient != null) {
                receiveFrameClient.stopReceiveFrameClient();
                receiveFrameClient.stop();
                receiveFrameClient = null;
            }
            if(receiveMessageClient != null){
                receiveMessageClient.stopReceiveMessageClient();
                receiveMessageClient = null;
            }
            if(auth != null){
                auth.close();
            }

            // Đóng kết nối socket và đặt biến isConnected = false để ngừng các tiến trình khác
            if (socket != null) {
                socket.close();
            }
            if (chatSocket != null) {
                chatSocket.close();
            }
            if (streamingSocket != null) {
                streamingSocket.close();
            }

            // Đảm bảo rằng không còn bất kỳ kết nối nào
            isConnected = false;

            instance = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

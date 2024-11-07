package com.remote.client.service;

import com.remote.client.infrastructure.SocketClient;
import com.remote.client.model.Message;

public class ServiceMessage {
    private SocketClient socketClient;

    public ServiceMessage() {
        this.socketClient = new SocketClient();
    }

    // Phương thức xử lý gửi message tới server
//    public String processMessage(Message message) {
//        // Có thể thêm logic xử lý dữ liệu trước khi gửi, như mã hóa, xác thực, v.v.
//        String response = "";
//                //socketClient.sendMessage(message.getContent());
//        return response;
//    }
}

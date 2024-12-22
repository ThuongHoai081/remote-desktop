package com.remote.client.infrastructure;

import java.io.IOException;

public class VoiceChatServer extends Thread{

    public VoiceChatServer() {
        start();
    }

    @Override
    public void run() {
        try {
            SocketServer.getStreamingInstance().voiceChat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


package com.remote.client.infrastructure;

public class VoiceChatClient extends Thread{
    public VoiceChatClient() {
        start();
    }

    @Override
    public void run() {
        SocketClient.getStreamingInstance().voiceChat();
    }
}

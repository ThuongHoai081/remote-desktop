package com.remote.client.service;

import com.remote.client.infrastructure.SocketServer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VoiceChatServer extends Thread{
    private SocketServer streamingSocket;
    private SourceDataLine speakers;
    private TargetDataLine microphone = null;

    public VoiceChatServer(SocketServer streamingSocket) {
        this.streamingSocket = streamingSocket;
        start();
    }

    @Override
    public void run() {

        try{
            InputStream in = streamingSocket.getInputStream();
            //audioformat
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            //audioformat
            //selecting and strating speakers
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();

            //for sending
            OutputStream out = null;
            out = streamingSocket.getOutputStream();

            //selecting and starting microphone
            microphone = AudioSystem.getTargetDataLine(format);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();


            byte[] bufferForOutput = new byte[4096];
            int bufferVariableForOutput = 0;

            byte[] bufferForInput = new byte[4096];
            int bufferVariableForInput;

            while (true) {
                // Đọc dữ liệu từ microphone và gửi qua socket
                if(microphone != null){
                    if ((bufferVariableForOutput = microphone.read(bufferForOutput, 0, 1024)) > 0) {
                        out.write(bufferForOutput, 0, bufferVariableForOutput);
                    }
                }

                // Nhận dữ liệu từ socket và phát qua speaker
                if(speakers != null){
                    if ((bufferVariableForInput = in.read(bufferForInput)) > 0 ) {
                        speakers.write(bufferForInput, 0, bufferVariableForInput);
                    }
                }
            }
        }
        catch(IOException | LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }


    public void cleanUp() {
        try {
            if (speakers != null && speakers.isOpen()) {
                speakers.stop();  // Dừng loa
                speakers.close(); // Đóng tài nguyên loa
                speakers = null;
                System.out.println("Speakers closed successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error while closing speakers: " + e.getMessage());
        }

        try {
            if (microphone != null && microphone.isOpen()) {
                microphone.stop();  // Dừng microphone
                microphone.close(); // Đóng tài nguyên microphone
                microphone = null;
                System.out.println("Microphone closed successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error while closing microphone: " + e.getMessage());
        }
    }

}

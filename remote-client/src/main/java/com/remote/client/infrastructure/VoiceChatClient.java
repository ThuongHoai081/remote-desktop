package com.remote.client.infrastructure;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VoiceChatClient extends Thread{

    private SocketClient streamingSocket;

    public VoiceChatClient(SocketClient streamingSocket) {
        this.streamingSocket = streamingSocket;
        start();
    }

    @Override
    public void run() {
        try{
            SourceDataLine speakers;
            TargetDataLine microphone = null;
            InputStream in = streamingSocket.getInputStream();
            //audioformat
            AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
            //audioformat
            //selecting and strating speakers
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            System.out.println("Starting speakers...");
            speakers.start();

            //for sending
            OutputStream out = null;
            out = streamingSocket.getOutPutStream();

            //selecting and starting microphone
            microphone = AudioSystem.getTargetDataLine(format);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            System.out.println("Starting microphone...");
            microphone.start();


            byte[] bufferForOutput = new byte[4096];
            int bufferVariableForOutput = 0;

            byte[] bufferForInput = new byte[4096];
            int bufferVariableForInput;

            while((bufferVariableForInput = in.read(bufferForInput)) > 0  || (bufferVariableForOutput=microphone.read(bufferForOutput, 0, 1024)) > 0) {
                out.write(bufferForOutput, 0, bufferVariableForOutput);
                speakers.write(bufferForInput, 0, bufferVariableForInput);
            }
        }
        catch(IOException | LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }
}
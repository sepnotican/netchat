package client;

import common.ServerConst;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class VoiceServerHandler {

    private Socket voiceSocket;

    public VoiceServerHandler() {

        try {
            voiceSocket = new Socket(ServerConst.SERVER_ADDR, ServerConst.VOICE_SERVER_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread txServerHandlerIn = new Thread(() -> {
            try {
                SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(ServerConst.AUDIO_FORMAT);

                sourceDataLine.open(ServerConst.AUDIO_FORMAT);
                sourceDataLine.start();

                InputStream is = voiceSocket.getInputStream();

                int buffSize = ServerConst.VOICE_BUFFER_SIZE;

                byte[] b = new byte[buffSize];
                BufferedInputStream bufferedInputStream = new BufferedInputStream(is, buffSize);

                //need buffer
                while (bufferedInputStream.read(b, 0, buffSize) > 0) {
                    sourceDataLine.write(b, 0, buffSize);
                }
                sourceDataLine.drain();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread txServerHandlerOut = new Thread(() -> {

            TargetDataLine microphone = null;
            try {
                microphone = AudioSystem.getTargetDataLine(ServerConst.AUDIO_FORMAT);
                microphone.open(ServerConst.AUDIO_FORMAT, ServerConst.VOICE_BUFFER_SIZE);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            //sending data to socket
            try {

                int numBytesRead;
                byte[] data = new byte[ServerConst.VOICE_BUFFER_SIZE];

                // Begin audio capture.
                microphone.start();

                OutputStream os = voiceSocket.getOutputStream();

                // Read the next chunk of data from the TargetDataLine.
                while ((numBytesRead = microphone.read(data, 0, data.length)) > 0) {
                    // Save this chunk of data.
                    os.write(data, 0, numBytesRead);
                }
                microphone.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        txServerHandlerOut.setDaemon(true);
        txServerHandlerOut.start();
        txServerHandlerIn.setDaemon(true);
        txServerHandlerIn.start();

    }
}

package common;

import javax.sound.sampled.AudioFormat;

public interface ServerConst {
    String SERVER_ADDR = "192.168.1.70";
    int SERVER_PORT = 8077;

    int VOICE_SERVER_PORT = 8088;
    float VOICE_SAMPLE_RATE = 8000.0f;
    int VOICE_SAMPLE_SIZE_BITS = 16;
    int VOICE_BUFFER_SIZE = 1024;

    AudioFormat AUDIO_FORMAT = new AudioFormat(ServerConst.VOICE_SAMPLE_RATE, ServerConst.VOICE_SAMPLE_SIZE_BITS, 1, true, true);
}

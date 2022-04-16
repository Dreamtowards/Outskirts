package outskirts.client.audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static outskirts.util.logging.Log.LOGGER;

public final class AudioEngine {

    private long device;
    private long context;

    public AudioEngine() {

        device = alcOpenDevice((ByteBuffer)null);
        if (device == 0)
            throw new IllegalStateException("Failed to open defualt audio device.");

        context = alcCreateContext(device, (IntBuffer)null);
        if (context == 0)
            throw new IllegalStateException("Failed to create OpenAL context.");
        alcMakeContextCurrent(context);

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        AL.createCapabilities(deviceCaps);


        alDistanceModel(AL_INVERSE_DISTANCE_CLAMPED);

        LOGGER.info("AudioEngine initialized. AL_I: {} / {}, devispec {}", alGetString(AL_VENDOR), alGetString(AL_VERSION), alcGetString(device, ALC_DEVICE_SPECIFIER));
    }


    public void destroy() {
        for (int s : AudioSource.srcs)
            alDeleteSources(s);
//        alDeleteBuffers();

        alcMakeContextCurrent(0);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    public void setListenerPosition(float x, float y, float z) {
        alListener3f(AL_POSITION, x, y, z);
        alListener3f(AL_VELOCITY, 0, 0, 0);
    }

    //tmp trans buffer for ListenerOrientation
    private FloatBuffer TMP_ORI_BUF_TRANS = BufferUtils.createFloatBuffer(6);

    /**
     * always is pos.xyz, vec(0, 1, 0).xyz
     */
    public void setListenerOrientation(float atX, float atY, float atZ, float upX, float upY, float upZ) {
        IOUtils.fillBuffer(TMP_ORI_BUF_TRANS, atX, atY, atZ, upX, upY, upZ);

        alListenerfv(AL_ORIENTATION, TMP_ORI_BUF_TRANS);
    }
}

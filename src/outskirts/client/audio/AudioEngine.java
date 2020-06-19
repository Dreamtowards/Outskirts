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
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryUtil.NULL;
import static outskirts.util.logging.Log.LOGGER;

public final class AudioEngine {

    private long alcContext;
    private long currentDevice;

    public AudioEngine() {
        try
        {
            currentDevice = alcOpenDevice((ByteBuffer)null);
            if (currentDevice == NULL)
                throw new IllegalStateException("Failed to open the default device.");

            ALCCapabilities deviceCaps = ALC.createCapabilities(currentDevice);

            alcContext = alcCreateContext(currentDevice, (IntBuffer)null);
            alcSetThreadContext(alcContext);
            AL.createCapabilities(deviceCaps);

            // AL.create();

            LOGGER.info("AudioEngine initialized. AL_I: {} / {}, devispec {}", alGetString(AL_VENDOR), alGetString(AL_VERSION), alcGetString(currentDevice, ALC_DEVICE_SPECIFIER));
        }
        catch (Throwable ex)
        {
            throw new RuntimeException("Failed to init OpenAL.", ex);
        }

        alDistanceModel(AL_INVERSE_DISTANCE_CLAMPED);
    }


    public void destroy() {
        for (int s : AudioSource.srcs)
            alDeleteSources(s);

        alcMakeContextCurrent(NULL);
        alcDestroyContext(alcContext);
        alcCloseDevice(currentDevice);
        // AL.destroy();
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

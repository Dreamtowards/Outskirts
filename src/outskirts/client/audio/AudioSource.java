package outskirts.client.audio;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;

public class AudioSource {

    static List<Integer> srcs = new ArrayList<>(); // AL Sources

    private final int sourceID;

    // method creating. cuz that had means have some special creating-sth and not just create a normal Object
    private AudioSource() {
        this.sourceID = alGenSources(); srcs.add(sourceID);

        alSourcef(sourceID, AL_ROLLOFF_FACTOR, 0.9f);
        alSourcef(sourceID, AL_REFERENCE_DISTANCE, 3);
    }

    public static AudioSource alfGenSource() {
        return new AudioSource();
    }

    public int sourceID() {
        return sourceID;
    }

    public void play() {
        alSourcePlay(sourceID);
    }
    public void pause() {
        alSourcePause(sourceID);
    }
    public void stop() {
        alSourceStop(sourceID);
    }


    public void setLooping(boolean looping) {
        alSourcei(sourceID, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    private int sourceState() {
        return alGetSourcei(sourceID, AL_SOURCE_STATE);
    }
    public boolean isPlaying() {
        return sourceState() == AL_PLAYING;
    }
    public boolean isPaused() {
        return sourceState() == AL_PAUSED;
    }
    public boolean isStopped() {
        return sourceState() == AL_STOPPED;
    }


    public void setVolume(float volume) {
        alSourcef(sourceID, AL_GAIN, volume);
    }
    public float getVolume() {
        return alGetSourcef(sourceID, AL_GAIN);
    }

    public void setPitch(float pitch) {
        alSourcef(sourceID, AL_PITCH, pitch);
    }
    public float getPitch() {
        return alGetSourcef(sourceID, AL_PITCH);
    }

    public void setPosition(float x, float y, float z) {
        alSource3f(sourceID, AL_POSITION, x, y, z);
    }

    public void setVelocity(float x, float y, float z) {
        alSource3f(sourceID, AL_VELOCITY, x, y, z);
    }




    public void queueBuffers(int bufferID) {
        alSourceQueueBuffers(sourceID, bufferID);
    }
    public int unqueueBuffers() {
        return alSourceUnqueueBuffers(sourceID);
    }

    public final void unqueueAllBuffers() { // for just play one buffer
        while (buffersQueued() != 0)
            unqueueBuffers();
    }

    public int buffersProcessed() {
        return alGetSourcei(sourceID, AL_BUFFERS_PROCESSED);
    }
    public int buffersQueued() {
        return alGetSourcei(sourceID, AL_BUFFERS_QUEUED);
    }


    public void delete() {
        stop();
        alDeleteSources(sourceID);
    }
}

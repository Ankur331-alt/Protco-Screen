package com.smart.rinoiot.voice.audio;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author edwin
 */
public class SoundPoolManager {

    private static final String TAG = "SoundPoolManager";

    /**
     * Wake up response mp3 audio file.
     */
    private static final String WAKE_UP_SOUND_MP3="audio/wake_response.mp3";

    /**
     * The SoundPool class instance.
     * For reference <a href="https://developer.android.com/reference/android/media/SoundPool">see</a>
     */
    private static SoundPool soundPool;

    /**
     * The sound pool manager instance
     */
    private static SoundPoolManager instance;

    /**
     * The pool map
     */
    private static final HashMap<String, Integer> SOUND_POOL_MAP = new HashMap<>();

    private SoundPoolManager(Context context){
        AudioAttributes attributes = new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
        soundPool = new SoundPool.Builder().setMaxStreams(3).setAudioAttributes(attributes).build();
        try {
            AssetManager am = context.getAssets();
            SOUND_POOL_MAP.put("wake-up", soundPool.load(am.openFd(WAKE_UP_SOUND_MP3), 1));
        } catch (IOException e) {
            Log.e(TAG, "create: Failed to create a sound pool. Cause=["+e.getLocalizedMessage()+"]");
        }

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) ->{
            Log.i(TAG, "create: " + sampleId);
        });
    }

    /**
     * Returns and instance of the manager
     * @param context the application context
     */
    public static SoundPoolManager getInstance(Context context) {
        if(null != instance){
            return instance;
        }

        synchronized (SoundPoolManager.class) {
            // create sound manager instance
            instance = new SoundPoolManager(context);
        }

        return instance;
    }

    /**
     * Releases the pool sound
     */
    public void release() {
        if (soundPool == null) {
            return;
        }
        soundPool.release();
        soundPool = null;
    }

    /**
     * Plays the audio
     * @param audioKey the name of the audio to be played
     */
    public void play(String audioKey) {
        if (soundPool == null) {
            Log.e(TAG, "play: sound pool is not initialized");
            return;
        }

        // Get the sound identifier
        Integer soundId = SOUND_POOL_MAP.get(audioKey);
        if(null == soundId){
            return;
        }

        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * Pause a playing audio file
     * @param audioKey audio key/file name
     */
    public void pause(String audioKey){
        if (soundPool == null) {
            Log.e(TAG, "pause: sound pool is not initialized");
            return;
        }

        // Get the sound identifier
        Integer soundId = SOUND_POOL_MAP.get(audioKey);
        if(null == soundId){
            return;
        }

        soundPool.pause(soundId);
    }

    /**
     * Stops a playing audio file
     *
     * @param audioKey audio key/file name
     */
    public void stop(String audioKey){
        if (soundPool == null) {
            Log.e(TAG, "stop: sound pool is not initialized");
            return;
        }

        // Get the sound identifier
        Integer soundId = SOUND_POOL_MAP.get(audioKey);
        if(null == soundId){
            return;
        }

        soundPool.stop(soundId);
    }
}

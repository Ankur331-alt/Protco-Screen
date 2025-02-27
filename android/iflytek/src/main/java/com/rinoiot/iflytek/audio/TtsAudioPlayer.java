package com.rinoiot.iflytek.audio;

import android.media.AudioTrack;
import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TtsAudioPlayer {
    private static final String TAG = "TtsAudioPlayer";
    private final Listener mListener;
    private final AtomicBoolean mIsLast;
    private final AudioTrack mAudioTrack;
    private final AtomicBoolean mIsPlaying;
    private final ExecutorService mExecutorService;
    private final ConcurrentLinkedQueue<byte[]> mAudioQueue;

    public TtsAudioPlayer(AudioTrack audioTrack, Listener listener) {
        // Initialize the thread pool, queue, and AudioTrack.
        mExecutorService = Executors.newSingleThreadExecutor();
        mAudioQueue = new ConcurrentLinkedQueue<>();
        mAudioTrack = audioTrack;
        mListener = listener;
        mIsLast = new AtomicBoolean(false);
        mIsPlaying = new AtomicBoolean(false);
    }

    public void write(byte[] data, boolean last) {
        mIsLast.set(last);
        mAudioQueue.add(data);
        if (!mIsPlaying.get()) {
            mIsPlaying.set(true);
            if(null != mListener){
                mListener.onStart();
            }
            mExecutorService.submit(this::play);
        }
    }

    private void play() {
        mAudioTrack.play();
        while (!mAudioQueue.isEmpty() || !mIsLast.get()) {
            byte[] data = mAudioQueue.poll();
            if (data != null) {
                int ret = mAudioTrack.write(data, 0, data.length);
                Log.d(TAG, "play: ret=" + ret + " | len=" + data.length);
            }else{
                Log.d(TAG, "play: data is null");
            }
        }

        // finished playing
        mIsLast.set(false);
        mIsPlaying.set(false);
        mAudioTrack.stop();
        mAudioTrack.flush();
        if(null != mListener){
            mListener.onStop();
        }
    }

    public void stop() {
        // these 2 conditions needs to happen
        // otherwise the player will keep running
        mAudioQueue.clear();
        mIsLast.set(true);

        // reset playing flag
        mIsPlaying.set(false);

        // clear audio track
        mAudioTrack.stop();
        mAudioTrack.flush();
    }

    public void release() {
        mIsLast.set(false);
        mIsPlaying.set(false);
        mAudioTrack.release();
        mExecutorService.shutdown();
    }

    public interface Listener {
        void onStart();
        void onStop();
    }
}
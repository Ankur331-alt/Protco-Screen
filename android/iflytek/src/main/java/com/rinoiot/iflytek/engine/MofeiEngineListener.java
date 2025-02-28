package com.rinoiot.iflytek.engine;

import com.rinoiot.iflytek.audio.SpeechStatus;
import com.rinoiot.iflytek.engine.model.tts.AudioStreamStatus;

/**
 * @author edwin
 */
public interface MofeiEngineListener {
    /**
     * Invoked on wake word/keyword is recognized
     */
    void onWakeup();

    /**
     * Invoked when text is detected
     * @param text text
     */
    void onResults(String text);

    /**
     * Invoked when the speech is started or completed
     * @param status the speech status
     */
    void onSpeech(SpeechStatus status);

    /**
     * Invoked when the audio stream start and complete
     * @param status audio stream status
     */
    void onAudioStream(AudioStreamStatus status);

    /**
     * Invoked on error events
     * @param code error code
     * @param message error message
     */
    void onError(int code, String message);
}

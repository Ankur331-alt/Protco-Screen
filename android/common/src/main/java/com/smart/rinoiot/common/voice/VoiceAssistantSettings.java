package com.smart.rinoiot.common.voice;

import java.io.Serializable;

public class VoiceAssistantSettings implements Serializable {
    boolean listening;

    boolean continuousMode;

    /**
     * The duration is in seconds
     */
    int continuousListeningDuration;

    public VoiceAssistantSettings() {
        this.listening = false;
        this.continuousMode = true;
        this.continuousListeningDuration = 10;
    }

    public VoiceAssistantSettings(boolean listening, int continuousListeningDuration) {
        this.listening = listening;
        this.continuousListeningDuration = continuousListeningDuration;
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public int getContinuousListeningDuration() {
        return continuousListeningDuration;
    }

    public void setContinuousListeningDuration(int continuousListeningDuration) {
        this.continuousListeningDuration = continuousListeningDuration;
    }

    public boolean isContinuousMode() {
        return continuousMode;
    }

    public void setContinuousMode(boolean continuousMode) {
        this.continuousMode = continuousMode;
    }
}

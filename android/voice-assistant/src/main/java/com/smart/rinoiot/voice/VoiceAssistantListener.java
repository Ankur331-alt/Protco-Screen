package com.smart.rinoiot.voice;

/**
 * @author edwin
 */
public interface VoiceAssistantListener {
    /**
     * Invoked when the assistant wakes ups
     */
    void onWakeUp();

    /**
     * Invoked when the assistant starts speaking
     *
     * @param text the text being spoken
     */
    void onSpeaking(String text);

    /**
     * Invoked when the user intent is received
     *
     * @param text the user's intent
     */
    void onIntent(String text);

    /**
     * Invoked when the assistant has finished speaking and commencing it's sleep mode
     */
    void onSleep();

    /**
     * Invoked when the assistant need to refresh the contents of the screen
     * @param target the part of the screen to be refreshed
     */
    void refresh(RefreshTarget target);
}

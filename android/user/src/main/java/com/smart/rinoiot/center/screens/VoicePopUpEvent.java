package com.smart.rinoiot.center.screens;

/**
 * @author edwin
 */
public class VoicePopUpEvent {

    /**
     * The event data
     */
    private final String data;

    /**
     * The event type
     */
    private final VoicePopUpEventType type;

    /**
     * The constructor
     *
     * @param type the event type
     */
    public VoicePopUpEvent(VoicePopUpEventType type) {
        this.data = "";
        this.type = type;
    }

    /**
     * The constructor
     *
     * @param data the event data
     * @param type the event type
     */
    public VoicePopUpEvent(String data, VoicePopUpEventType type) {
        this.data = data;
        this.type = type;
    }

    /**
     * Getter for the event data
     * @return the event data
     */
    public String getData() {
        return data;
    }

    /**
     * Getter for the event type
     * @return the event type
     */
    public VoicePopUpEventType getType() {
        return type;
    }
}

package com.rinoiot.iflytek.audio;

/**
 * @author edwin
 */
public class AudioConfig {

    /**
     * 声卡号
     */
    private int card;

    /**
     * 声卡设备号
     */
    private int device;

    /**
     * 输入音频的声道数量
     */
    private int channel;

    /**
     * true(对输入音频做处理),false(不处理），搭配channelParams使用
     */
    private boolean processInput;

    /**
     * The Audio config
     *
     * @param card 声卡号
     * @param device 声卡设备号
     * @param channel 输入音频的声道数量
     * @param processInput true(对输入音频做处理),false(不处理），搭配channelParams使用
     */
    public AudioConfig(int card, int device, int channel, boolean processInput) {
        this.card = card;
        this.device = device;
        this.channel = channel;
        this.processInput = processInput;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isProcessInput() {
        return processInput;
    }

    public void setProcessInput(boolean processInput) {
        this.processInput = processInput;
    }
}

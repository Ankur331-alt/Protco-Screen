package com.rinoiot.iflytek.utils;

/**
 * @author edwin
 */
public class AudioFilter {

    public static byte[] filter(int inChannel, int audioFormat, byte[] rawAudio, int outputChannel) {
        int bytesPerSample = audioFormat == 1 ? 4 : 2;
        int samplesPerChannel = rawAudio.length / (inChannel * bytesPerSample);
        int inputDataLenPerSample = inChannel * bytesPerSample;
        int outputDataLenPerSample = outputChannel * bytesPerSample;

        // Create a new byte array for the desired channels
        byte[] filteredData = new byte[rawAudio.length / inChannel * outputChannel];

        // Extract and copy the samples from the desired channel
        for (int i = 0; i < outputChannel; ++i) {
            // Calculate the target channel after reordering
            int channel = (i < 4) ? ((i + 2) % 4) : i;
            for (int j = 0; j < samplesPerChannel; ++j) {
                int targetOffset = channel * bytesPerSample + outputDataLenPerSample * j;
                int sourceOffset = (i + 2) * bytesPerSample + inputDataLenPerSample * j;
                System.arraycopy(rawAudio, sourceOffset, filteredData, targetOffset, bytesPerSample);
            }
        }

        return filteredData;
    }

    /**
     * Returns the filtered audio data.
     * ToDo() complete implementation (an ArrayIndexOutOfBoundsException is thrown)
     * @param audioFormat the raw audio format.
     * @param rawAudio the raw audio data.
     * @param desiredChannels the desired channels.
     * @return the filtered audio data.
     */
    public static byte[] filter(
            int inChannel,
            int audioFormat,
            byte[] rawAudio,
            int[] desiredChannels
    ) {
        // Number of bytes per sample
        int bytesPerSample = (audioFormat == 1) ? 4 : 2;
        int samplesPerChannel = rawAudio.length / (bytesPerSample * inChannel);

        // Create a new byte array for the desired channels
        byte[] filteredData = new byte[desiredChannels.length * samplesPerChannel * bytesPerSample];

        // Extract and copy the samples from the desired channels
        for (int i = 0; i < desiredChannels.length; i++) {
            int desiredChannel = desiredChannels[i];
            for (int j = 0; j < samplesPerChannel; ++j) {
                int targetOffset = i * bytesPerSample + desiredChannel * bytesPerSample * j;
                int sourceOffset =  (i + 2) * bytesPerSample + inChannel * bytesPerSample * j;
                System.arraycopy(rawAudio, sourceOffset, filteredData, targetOffset, bytesPerSample);
            }
        }
        return filteredData;
    }
}

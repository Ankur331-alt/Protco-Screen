package com.smart.rinoiot.voice.audio;

import android.content.Context;
import android.util.Log;

import com.smart.rinoiot.common.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * @author edwin
 */
public class ProcessingAckManager {

    private static final String TAG = "ProcessingAckManager";

    /**
     * The sound pool manager instance
     */
    private static ProcessingAckManager instance;

    /**
     * Pseudorandom number generator (PRNG)
     */
    private static final Random RANDOM_NUM_GENERATOR = new Random();

    /**
     * The processing acknowledgement file
     */
    private static final String PROCESSING_ACK_FILE = "processing_ack.txt";

    /**
     * The processing acknowledgement data
     */
    private static final List<String> ACKNOWLEDGEMENTS = new ArrayList<>();

    private ProcessingAckManager(@ApplicationContext Context context) {
        try {
            String[] acknowledgements = FileUtils.readFile(
                    context, PROCESSING_ACK_FILE, "utf-8").split("\\r?\\n"
            );
            ACKNOWLEDGEMENTS.addAll(Arrays.asList(acknowledgements));
        }catch (Exception exception) {
            Log.d(TAG, "Failed to load. Cause=" + exception.getLocalizedMessage());
        }
    }

    /**
     * Returns and instance of the manager
     * @param context the application context
     */
    public static ProcessingAckManager getInstance(Context context) {
        if(null != instance){
            return instance;
        }

        synchronized (SoundPoolManager.class) {
            // create sound manager instance
            instance = new ProcessingAckManager(context);
        }
        return instance;
    }

    /**
     * Returns a random processing acknowledgement
     *
     * @return a random processing acknowledgement
     */
    public String getAck() {
        if(ACKNOWLEDGEMENTS.size() == 0){
            return "";
        }
        int bound = ACKNOWLEDGEMENTS.size() - 1;
        int index = RANDOM_NUM_GENERATOR.nextInt(bound) + 1;
        return ACKNOWLEDGEMENTS.get(index);
    }
}

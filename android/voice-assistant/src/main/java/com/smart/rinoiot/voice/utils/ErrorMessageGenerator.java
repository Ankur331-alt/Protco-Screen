package com.smart.rinoiot.voice.utils;

import java.util.Random;

public class ErrorMessageGenerator {
    private static final String[] errorMessages = {
            "Oops! There seems to be an issue with your request. Please give it another try.",
            "We apologize for the inconvenience, but there was an error in processing your request. Please attempt it again.",
            "Sorry, there was a glitch in processing your request. Kindly retry.",
            "Regrettably, we encountered an error while handling your request. Please try again later.",
            "Apologies, but we hit a snag while processing your request. Could you please try once more?",
            "We're sorry, but something went haywire while processing your request. Please give it another shot.",
            "Oh no! We encountered a problem processing your request. Please try again in a moment.",
            "We apologize for the inconvenience caused. It appears there was an error in processing your request. Please retry now.",
            "Unfortunately, there was a technical hiccup in handling your request. Please try again to continue.",
            "Oops! It looks like there was a mishap while processing your request. Kindly try once more."
    };

    public static String getRandomErrorMessage() {
        Random random = new Random();
        int index = random.nextInt(errorMessages.length);
        return errorMessages[index];
    }
}

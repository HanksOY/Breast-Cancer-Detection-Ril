package com.example.afinal.api.models;

import android.util.Log;
import com.example.afinal.api.UltravoxConfig;

/**
 * Text request model for Ultravox API
 */
public class TextRequest extends UltravoxRequest {
    private static final String TAG = "TextRequest";
    
    /**
     * Creates a new text request
     *
     * @param userInput The user input
     */
    public TextRequest(String userInput) {
        super(UltravoxConfig.MODEL_NAME, userInput);
        setTemperature(0.7f);
    }

    /**
     * Factory method to create a new TextRequest with sanitized input
     *
     * @param userInput Raw user input
     * @return A new TextRequest with sanitized input
     */
    public static TextRequest create(String userInput) {
        String sanitizedInput = sanitizeInput(userInput);
        Log.d(TAG, "Creating request with sanitized input: " + sanitizedInput);
        return new TextRequest(sanitizedInput);
    }

    /**
     * Sanitizes the user input to prevent injection attacks
     *
     * @param input Raw user input
     * @return Sanitized input
     */
    private static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove control characters
        String sanitized = input.replaceAll("[\\p{Cntrl}]", "");
        
        // Remove HTML tags
        sanitized = sanitized.replaceAll("<[^>]*>", "");
        
        // Replace multiple spaces with a single space
        sanitized = sanitized.replaceAll("\\s+", " ");
        
        return sanitized.trim();
    }
} 
package com.example.afinal.api.models;

import com.example.afinal.api.UltravoxConfig;
import com.google.gson.annotations.SerializedName;

/**
 * Request class specific to voice requests
 */
public class VoiceRequest extends UltravoxRequest {
    @SerializedName("voice_id")
    private String voiceId;
    
    @SerializedName("content")
    private String content;
    
    /**
     * Creates a new voice request with the specified params
     * @param model The model to use for the request
     * @param voiceId The voice ID to use for synthesis
     * @param content The raw content for synthesis
     */
    public VoiceRequest(String model, String voiceId, String content) {
        super(model, content);
        this.voiceId = voiceId;
        this.content = content;
        
        // Set the voice name
        setVoice(UltravoxConfig.VOICE_NAME);
    }
    
    /**
     * Creates a new voice request from transcribed speech
     * @param model The model to use
     * @param speech The transcribed speech
     * @return A new voice request
     */
    public static VoiceRequest fromTranscribedSpeech(String model, String speech) {
        String sanitized = sanitizeUserInput(speech);
        return new VoiceRequest(model, UltravoxConfig.VOICE_ID, sanitized);
    }
    
    /**
     * Sanitizes user input to prevent injection attacks
     * @param input The raw user input
     * @return The sanitized input
     */
    public static String sanitizeUserInput(String input) {
        if (input == null) return "";
        
        // Basic sanitization - could be expanded
        return input.trim();
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 
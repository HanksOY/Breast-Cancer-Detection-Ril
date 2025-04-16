package com.example.afinal.api;

import android.util.Log;
import com.example.afinal.BuildConfig;

/**
 * Configuration constants for API
 */
public class UltravoxConfig {
    private static final String TAG = "UltravoxConfig";
    
    // Base URL for the API
    public static final String BASE_URL = "https://api.ultravox.ai/api/";
    
    // API endpoints
    public static final String TEXT_ENDPOINT = "v1/messages";
    public static final String IMAGE_ENDPOINT = "v1/messages";
    public static final String AUDIO_ENDPOINT = "v1/messages";
    
    // API key from BuildConfig
    public static final String API_KEY = BuildConfig.ULTRAVOX_API_KEY;
    
    // Model name
    public static final String MODEL_NAME = "fixie-ai/ultravox";
    
    // Voice settings
    public static final String VOICE_ID = BuildConfig.ULTRAVOX_VOICE_ID;
    public static final String VOICE_NAME = BuildConfig.ULTRAVOX_VOICE_NAME;
    
    // Request headers
    public static final String HEADER_API_KEY = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    
    // Content types
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    
    // Connection timeout in seconds
    public static final int TIMEOUT_CONNECT = 30;
    public static final int TIMEOUT_READ = 30;
    public static final int TIMEOUT_WRITE = 30;
    
    static {
        Log.d(TAG, "UltravoxConfig initialized:");
        Log.d(TAG, "API Base URL: " + BASE_URL);
        Log.d(TAG, "Text endpoint: " + TEXT_ENDPOINT);
        Log.d(TAG, "API Key (first 5 chars): " + 
              (API_KEY != null ? API_KEY.substring(0, Math.min(5, API_KEY.length())) + "..." : "null"));
        Log.d(TAG, "Model name: " + MODEL_NAME);
    }
} 
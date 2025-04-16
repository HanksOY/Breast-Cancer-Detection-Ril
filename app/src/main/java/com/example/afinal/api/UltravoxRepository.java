package com.example.afinal.api;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.afinal.api.models.ImageRequest;
import com.example.afinal.api.models.StreamResponse;
import com.example.afinal.api.models.TextRequest;
import com.example.afinal.api.models.VoiceRequest;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for API interactions
 */
public class UltravoxRepository {
    
    private static final String TAG = "UltravoxRepository";
    private static volatile UltravoxRepository instance;
    
    private final UltravoxService service;
    private final String authHeader;
    private final String anthropicVersion = "2023-06-01";
    private final Gson gson;
    
    private UltravoxRepository() {
        service = UltravoxClient.getInstance().getService();
        authHeader = UltravoxClient.formatAuthHeader(UltravoxConfig.API_KEY);
        gson = new Gson();
        
        // Log initialization for debugging
        Log.d(TAG, "UltravoxRepository initialized");
        Log.d(TAG, "API Key (first few chars): " +
                UltravoxConfig.API_KEY.substring(0, 5) + "...");
        Log.d(TAG, "Auth header (processed): " +
                authHeader.substring(0, 5) + "...");
        Log.d(TAG, "Model name: " + UltravoxConfig.MODEL_NAME);
        Log.d(TAG, "Anthropic version: " + anthropicVersion);
    }
    
    /**
     * Get singleton instance of UltravoxRepository
     */
    public static UltravoxRepository getInstance() {
        if (instance == null) {
            synchronized (UltravoxRepository.class) {
                if (instance == null) {
                    instance = new UltravoxRepository();
                }
            }
        }
        return instance;
    }
    
    /**
     * Reset the singleton instance (useful for testing)
     */
    public static void resetInstance() {
        synchronized (UltravoxRepository.class) {
            instance = null;
        }
    }
    
    /**
     * Send a text request to the API
     * @param text User's text input
     * @param callback Callback for response
     */
    public void sendTextRequest(String text, ResponseCallback<com.example.afinal.api.models.UltravoxResponse> callback) {
        // Create text request
        TextRequest request = TextRequest.create(text);
        
        // Log request details
        Log.d(TAG, "Sending text request: " + text);
        Log.d(TAG, "Using API key: " + authHeader.substring(0, Math.min(5, authHeader.length())) + "...");
        
        // Add detailed JSON logging of the full request
        Log.d("UltravoxRequest", new Gson().toJson(request));
        
        // Send request
        service.sendTextRequest(anthropicVersion, authHeader, UltravoxConfig.CONTENT_TYPE_JSON, request).enqueue(new Callback<com.example.afinal.api.models.UltravoxResponse>() {
            @Override
            public void onResponse(Call<com.example.afinal.api.models.UltravoxResponse> call, Response<com.example.afinal.api.models.UltravoxResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Text request succeeded with status: " + response.code());
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                        Log.e(TAG, "Text request failed with status: " + response.code() + ", error: " + errorMessage);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    callback.onError(new Exception(errorMessage));
                }
            }
            
            @Override
            public void onFailure(Call<com.example.afinal.api.models.UltravoxResponse> call, Throwable t) {
                Log.e(TAG, "Text request network failure", t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    /**
     * Send a streaming text request to the API
     * @param text User's text input
     * @param callback Callback for streaming response
     */
    public void sendTextRequestStreaming(String text, StreamingCallback callback) {
        Log.d(TAG, "Sending streaming text request: " + text);
        
        // Create text request
        TextRequest request = TextRequest.create(text);
        
        // Log request details
        Log.d(TAG, "Request model: " + request.getModel());
        Log.d(TAG, "Auth header: " + authHeader.substring(0, 15) + "...");
        
        // Add detailed JSON logging of the full request
        Log.d("UltravoxRequest", new Gson().toJson(request));
        
        // Send streaming request
        service.sendTextRequestStreaming(anthropicVersion, authHeader, UltravoxConfig.CONTENT_TYPE_JSON, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Streaming response successful, code: " + response.code());
                    try {
                        InputStream inputStream = response.body().byteStream();
                        processStreamingResponse(inputStream, callback);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing stream: " + e.getMessage(), e);
                        callback.onError(new Exception("Error processing stream: " + e.getMessage(), e));
                    }
                } else {
                    String errorMessage = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                        Log.e(TAG, "Error response: " + errorMessage);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "Request failed: " + t.getMessage(), t);
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    /**
     * Send a streaming image request to the API
     * @param bitmap Image to analyze
     * @param description Optional description of the image
     * @param callback Callback for streaming response
     */
    public void sendImageRequestStreaming(Bitmap bitmap, String description, StreamingCallback callback) {
        // Convert bitmap to base64
        String base64Image = bitmapToBase64(bitmap);
        
        // Create image request
        ImageRequest request = ImageRequest.create(base64Image, description);
        
        // Send streaming request
        service.sendImageRequestStreaming(anthropicVersion, authHeader, UltravoxConfig.CONTENT_TYPE_JSON, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        InputStream inputStream = response.body().byteStream();
                        processStreamingResponse(inputStream, callback);
                    } catch (Exception e) {
                        callback.onError(new Exception("Error processing stream: " + e.getMessage(), e));
                    }
                } else {
                    String errorMessage = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    /**
     * Send a voice request for TTS
     * @param text Text to convert to speech
     * @param callback Callback for audio response
     */
    public void sendVoiceRequest(String text, ResponseCallback<InputStream> callback) {
        // Create voice request
        VoiceRequest request = VoiceRequest.create(text);
        
        // Send request
        service.sendVoiceRequest(anthropicVersion, authHeader, UltravoxConfig.CONTENT_TYPE_JSON, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().byteStream());
                } else {
                    String errorMessage = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }
    
    /**
     * Process streaming response from the API
     * @param inputStream Input stream from response
     * @param callback Streaming callback
     */
    private void processStreamingResponse(InputStream inputStream, StreamingCallback callback) {
        Log.d(TAG, "Starting to process streaming response");
        
        new Thread(() -> {
            StringBuilder line = new StringBuilder();
            int c;
            int totalContentReceived = 0;
            
            try {
                while ((c = inputStream.read()) != -1) {
                    if (c == '\n') {
                        String jsonLine = line.toString().trim();
                        line.setLength(0);
                        
                        if (!jsonLine.isEmpty()) {
                            Log.d(TAG, "Received line: " + (jsonLine.length() > 100 ? jsonLine.substring(0, 100) + "..." : jsonLine));
                        }
                        
                        if (!jsonLine.isEmpty() && !jsonLine.equals("data: [DONE]")) {
                            // Check if the line starts with "data: " and remove it if present
                            if (jsonLine.startsWith("data: ")) {
                                jsonLine = jsonLine.substring(6);
                            }
                            
                            try {
                                StreamResponse streamResponse = gson.fromJson(jsonLine, StreamResponse.class);
                                if (streamResponse != null) {
                                    String content = streamResponse.getDeltaContent();
                                    if (content != null && !content.isEmpty()) {
                                        totalContentReceived += content.length();
                                        callback.onContent(content, streamResponse.isDone());
                                        Log.d(TAG, "Content delta: " + content + " (Total: " + totalContentReceived + ")");
                                    }
                                    
                                    if (streamResponse.isDone()) {
                                        Log.d(TAG, "Stream complete signal received");
                                        callback.onComplete();
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing JSON: " + jsonLine, e);
                            }
                        } else if (jsonLine.equals("data: [DONE]")) {
                            Log.d(TAG, "Received DONE signal");
                            callback.onComplete();
                            break;
                        }
                    } else {
                        line.append((char) c);
                    }
                }
                Log.d(TAG, "End of stream reached, total content received: " + totalContentReceived + " chars");
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error reading stream: " + e.getMessage(), e);
                callback.onError(new Exception("Error reading stream: " + e.getMessage(), e));
            }
        }).start();
    }
    
    /**
     * Convert a bitmap to base64 string
     * @param bitmap Bitmap to convert
     * @return Base64 encoded string
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    
    /**
     * Callback interface for API responses
     */
    public interface ResponseCallback<T> {
        void onSuccess(T response);
        void onError(Exception error);
    }
    
    /**
     * Callback interface for streaming responses
     */
    public interface StreamingCallback {
        void onContent(String content, boolean isDone);
        void onComplete();
        void onError(Exception error);
    }
} 
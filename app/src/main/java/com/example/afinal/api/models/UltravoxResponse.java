package com.example.afinal.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Base response model for Ultravox API
 */
public class UltravoxResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("model")
    private String model;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("error")
    private ErrorInfo error;
    
    public String getId() {
        return id;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getModel() {
        return model;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public boolean hasError() {
        return error != null;
    }
    
    public ErrorInfo getError() {
        return error;
    }
    
    /**
     * Error information returned by the API
     */
    public static class ErrorInfo {
        @SerializedName("message")
        private String message;
        
        @SerializedName("code")
        private String code;
        
        public String getMessage() {
            return message;
        }
        
        public String getCode() {
            return code;
        }
        
        @Override
        public String toString() {
            return "Error " + code + ": " + message;
        }
    }
} 
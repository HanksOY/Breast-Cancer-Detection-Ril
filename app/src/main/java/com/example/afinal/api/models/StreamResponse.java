package com.example.afinal.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for Ultravox API streaming responses
 */
public class StreamResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("streaming")
    private boolean streaming;
    
    @SerializedName("created")
    private long created;
    
    @SerializedName("model")
    private String model;
    
    @SerializedName("object")
    private String object;
    
    @SerializedName("choices")
    private Choice[] choices;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("delta")
    private Delta delta;
    
    @SerializedName("done")
    private boolean done;
    
    /**
     * Get the delta content
     */
    public String getDeltaContent() {
        // First try to get from delta if available
        if (delta != null && delta.content != null) {
            return delta.content;
        }
        
        // Then try to get from content field
        if (content != null) {
            return content;
        }
        
        // Then try to get from choices if available
        if (choices != null && choices.length > 0) {
            if (choices[0].delta != null && choices[0].delta.content != null) {
                return choices[0].delta.content;
            }
            if (choices[0].text != null) {
                return choices[0].text;
            }
        }
        
        // If all else fails, return empty string
        return "";
    }
    
    /**
     * Check if this is the final response 
     */
    public boolean isDone() {
        return done;
    }
    
    /**
     * Get the ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the model
     */
    public String getModel() {
        return model;
    }
    
    /**
     * Delta structure in a streaming response
     */
    public static class Delta {
        @SerializedName("content")
        private String content;
        
        /**
         * Get the content
         */
        public String getContent() {
            return content;
        }
    }
    
    /**
     * Choice in a streaming response
     */
    public static class Choice {
        @SerializedName("text")
        private String text;
        
        @SerializedName("delta")
        private Delta delta;
        
        @SerializedName("index")
        private int index;
        
        @SerializedName("finish_reason")
        private String finishReason;
        
        /**
         * Get the text
         */
        public String getText() {
            return text;
        }
        
        /**
         * Get the index
         */
        public int getIndex() {
            return index;
        }
        
        /**
         * Get the finish reason
         */
        public String getFinishReason() {
            return finishReason;
        }
    }
} 
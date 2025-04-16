package com.example.afinal.api.models;

import com.google.gson.annotations.SerializedName;
import com.example.afinal.api.UltravoxConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * Base request class for the API
 */
public class UltravoxRequest {
    @SerializedName("model")
    private String model;
    
    @SerializedName("messages")
    private List<Message> messages;
    
    @SerializedName("system")
    private String system;
    
    @SerializedName("temperature")
    private float temperature;
    
    @SerializedName("max_tokens")
    private int maxTokens;
    
    @SerializedName("voice")
    private String voice;
    
    // System prompt prefix for all interactions
    private static final String SYSTEM_PROMPT = 
            "You are Ril, an AI assistant specializing in breast cancer detection and treatment. " +
            "Your voice is professional, compassionate, and clear. " +
            "Provide factual, evidence-based information to users. " +
            "Keep responses concise and focused on breast cancer topics. " +
            "If unsure about medical advice, clarify this and suggest consulting healthcare professionals.";

    /**
     * Constants for message roles in API
     */
    public static final class Roles {
        public static final String USER = "user";
        public static final String ASSISTANT = "assistant";
        
        private Roles() {} // Prevent instantiation
    }

    /**
     * Creates a new request with the specified model and prompt
     * @param model The model to use for the request
     * @param userInput The user input
     */
    public UltravoxRequest(String model, String userInput) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message(Roles.USER, userInput));
        this.system = SYSTEM_PROMPT;
        this.voice = UltravoxConfig.VOICE_NAME;
        
        // Default values
        this.temperature = 0.7f;
        this.maxTokens = 1024;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public void addMessage(String content) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(new Message(Roles.USER, content));
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    
    public String getSystem() {
        return system;
    }
    
    public void setSystem(String system) {
        this.system = system;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public String getVoice() {
        return voice;
    }
    
    public void setVoice(String voice) {
        this.voice = voice;
    }
    
    /**
     * Message class for messages array
     */
    public static class Message {
        @SerializedName("role")
        private String role;
        
        @SerializedName("content")
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
} 
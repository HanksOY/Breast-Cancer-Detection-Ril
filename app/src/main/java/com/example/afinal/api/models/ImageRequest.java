package com.example.afinal.api.models;

import com.example.afinal.api.UltravoxConfig;
import com.google.gson.annotations.SerializedName;

/**
 * Request model for image analysis
 */
public class ImageRequest extends UltravoxRequest {
    
    @SerializedName("image")
    private String imageData;
    
    @SerializedName("imageDescription")
    private String description;
    
    public ImageRequest(String userInput, String imageData, String description) {
        super(UltravoxConfig.MODEL_NAME, userInput);
        this.imageData = imageData;
        this.description = description;
    }
    
    /**
     * Create an image request with base64 encoded image data
     */
    public static ImageRequest create(String base64Image, String description) {
        String sanitizedDescription = sanitizeInput(description);
        // Default input for image analysis
        String userInput = "Analyze this mammogram image for breast cancer diagnostics. ";
        if (!sanitizedDescription.isEmpty()) {
            userInput += "Additional context: " + sanitizedDescription;
        }
        
        return new ImageRequest(userInput, base64Image, sanitizedDescription);
    }
    
    public String getImageData() {
        return imageData;
    }
    
    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Sanitizes user input to prevent injection attacks
     */
    private static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove control characters and strip HTML tags
        String sanitized = input.replaceAll("[\\p{Cntrl}]", " ")
                          .replaceAll("<[^>]*>", "")
                          .trim();
        
        // Replace multiple spaces with single space
        sanitized = sanitized.replaceAll("\\s+", " ");
        
        return sanitized;
    }
} 
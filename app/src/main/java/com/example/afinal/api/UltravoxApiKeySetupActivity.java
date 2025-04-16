package com.example.afinal.api;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.R;

/**
 * Activity for setting up the Ultravox API key
 */
public class UltravoxApiKeySetupActivity extends AppCompatActivity {
    
    private EditText apiKeyInput;
    private TextView instructionsText;
    private Button saveButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_key_setup);
        
        // Initialize views
        apiKeyInput = findViewById(R.id.apiKeyInput);
        instructionsText = findViewById(R.id.instructionsText);
        saveButton = findViewById(R.id.saveButton);
        
        // Set instructions
        instructionsText.setText(
            "To use the Ultravox API, you need a valid API key from your Ultravox account.\n\n" +
            "1. Visit https://app.ultravox.ai/settings/\n" +
            "2. Sign in to your Ultravox account\n" +
            "3. Go to the 'API Keys' section\n" +
            "4. Click 'Generate New Key'\n" +
            "5. Copy the new API key\n" +
            "6. Paste it below and tap 'Save'\n\n" +
            "After getting your API key, update it in your app/build.gradle.kts file:\n" +
            "buildConfigField(\"String\", \"ULTRAVOX_API_KEY\", \"\\\"YOUR_API_KEY_HERE\\\"\")"
        );
        
        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiKey = apiKeyInput.getText().toString().trim();
                if (TextUtils.isEmpty(apiKey)) {
                    Toast.makeText(UltravoxApiKeySetupActivity.this, 
                            "Please enter an API key", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Normally you would save this to a secure storage
                // For a production app, use Android Keystore or AndroidX Security libraries
                Toast.makeText(UltravoxApiKeySetupActivity.this, 
                        "API key saved! For permanent use, update your build.gradle.kts file", 
                        Toast.LENGTH_LONG).show();
                
                // Test the API key with a simple request
                testApiKey(apiKey);
            }
        });
    }
    
    /**
     * Test the API key with a simple request
     * @param apiKey The API key to test
     */
    private void testApiKey(String apiKey) {
        // This is just a placeholder - in a real app, you'd make an actual API request
        Toast.makeText(this, "Testing API key...", Toast.LENGTH_SHORT).show();
        
        // For a real implementation, you might:
        // 1. Create a temporary repository instance with the new API key
        // 2. Make a simple API call to verify the key works
        // 3. Show success/error based on the response
    }
} 
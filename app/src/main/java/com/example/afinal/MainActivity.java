package com.example.afinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.adapters.MessageAdapter;
import com.example.afinal.models.Message;
import android.app.ProgressDialog;

import java.io.InputStream;
import android.content.ContentValues;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import android.view.View;

public class MainActivity extends AppCompatActivity {
    // View elements
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton micButton;
    private ImageButton attachButton;
    private ImageButton fileButton;
    private Button testApiButton;
    private MessageAdapter messageAdapter;
    
    private SpeechRecognizer speechRecognizer;
    private boolean isInConversationMode = false;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    private Uri cameraImageUri;
    private MediaPlayer mediaPlayer;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        micButton = findViewById(R.id.micButton);
        attachButton = findViewById(R.id.attachButton);
        fileButton = findViewById(R.id.fileButton);
        testApiButton = findViewById(R.id.testApiButton);
        
        // Setup UI components
        setupChatRecyclerView();
        setupSendButton();
        setupSpeechRecognizer();
        setupMicButton();
        setupImageButton();
        setupFileButton();
        setupTestApiButton();
        
        // Initialize permission launcher here BEFORE activity is STARTED
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startConversationMode();
                } else {
                    Toast.makeText(this, "Microphone permission is required for voice input", Toast.LENGTH_LONG).show();
                }
            }
        );
        
        // Initialize storage permission launcher
        storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Storage permission is required to select images", Toast.LENGTH_LONG).show();
                }
            }
        );
        
        // Initialize camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to capture images", Toast.LENGTH_LONG).show();
                }
            }
        );
        
        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        handleSelectedImage(selectedImageUri);
                    }
                }
            }
        );
        
        // Initialize camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success && cameraImageUri != null) {
                    handleSelectedImage(cameraImageUri);
                }
            }
        );

        // Add initial welcome message
        addAssistantMessage("Hello, I'm Ril, your advanced medical assistant. How can I help with your breast cancer diagnostics today?");
    }
    
    private void setupChatRecyclerView() {
        messageAdapter = new MessageAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageAdapter);
    }
    
    private void setupSendButton() {
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                // Add user message to chat
                addUserMessage(message);
                
                // Process the message and generate a response
                processUserMessage(message);
                
                // Clear the input field
                messageInput.setText("");
            }
        });
    }
    
    private void addUserMessage(String content) {
        Message message = new Message(content, Message.TYPE_USER);
        messageAdapter.addMessage(message);
        scrollToBottom();
    }
    
    private void addAssistantMessage(String content) {
        Message message = new Message(content, Message.TYPE_ASSISTANT);
        messageAdapter.addMessage(message);
        scrollToBottom();
    }
    
    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
    
    private void processUserMessage(String message) {
        // Use Ultravox API to process user message
        if (TextUtils.isEmpty(message)) {
            return;
        }
        
        // Get the Ultravox repository instance
        com.example.afinal.api.UltravoxRepository repository = com.example.afinal.api.UltravoxRepository.getInstance();
        
        // Show a loading message
        Message typingMessage = new Message("Thinking...", Message.TYPE_ASSISTANT);
        messageAdapter.addMessage(typingMessage);
        scrollToBottom();
        
        // Use streaming API for better user experience
        repository.sendTextRequestStreaming(message, new com.example.afinal.api.UltravoxRepository.StreamingCallback() {
            final StringBuilder responseBuilder = new StringBuilder();
            
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onContent(String content, boolean isDone) {
                responseBuilder.append(content);
                
                // Update assistant message with the latest content
                runOnUiThread(() -> {
                    typingMessage.updateContent(responseBuilder.toString());
                    messageAdapter.notifyDataSetChanged();
                });
            }
            
            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    // Remove the typing message and add the final response
                    messageAdapter.removeMessage(typingMessage);
                    String finalResponse = responseBuilder.toString();
                    addAssistantMessage(finalResponse);
                    
                    // Implement TTS for voice output
                    convertTextToSpeech(finalResponse);
                });
            }
            
            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    // Remove the typing message and add an error message
                    messageAdapter.removeMessage(typingMessage);
                    addAssistantMessage("I'm sorry, I encountered an error: " + error.getMessage());
                });
            }
        });
    }
    
    private void setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            createSpeechRecognizer();
        } else {
            Toast.makeText(this, "Speech recognition is not available on this device", Toast.LENGTH_LONG).show();
        }
    }
    
    private void createSpeechRecognizer() {
        // Destroy any existing recognizer first
        if (speechRecognizer != null) {
            try {
                speechRecognizer.destroy();
                speechRecognizer = null;
            } catch (Exception e) {
                // Silent catch
            }
        }
        
        // Create a new speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float rmsdB) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onError(int error) {
                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    // No speech detected, continue listening only if still in conversation mode
                    if (isInConversationMode) {
                        startListening();
                    }
                }
            }
            @Override
            public void onResults(Bundle results) {
                try {
                    java.util.ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String text = matches.get(0);
                        messageInput.setText(text);
                        // Process the recognized text
                        processVoiceInput(text);
                    }
                    // Continue listening in conversation mode
                    if (isInConversationMode) {
                        startListening();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error processing speech: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onPartialResults(Bundle partialResults) {}
            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    // Set up the mic button
    private void setupMicButton() {
        micButton.setOnClickListener(v -> {
            if (!isInConversationMode) {
                checkPermissionAndStartConversation();
            } else {
                stopConversationMode();
            }
        });
    }

    private void checkPermissionAndStartConversation() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED) {
                startConversationMode();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
            )) {
                Toast.makeText(this, "Microphone permission is required for voice input", Toast.LENGTH_LONG).show();
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error requesting permission: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startConversationMode() {
        try {
            // Make sure we have a fresh recognizer
            createSpeechRecognizer();
            
            isInConversationMode = true;
            updateMicButtonAppearance();
            
            // Show a toast to indicate that conversation mode is active
            Toast.makeText(this, "Conversation mode active - speak now", Toast.LENGTH_SHORT).show();
            
            startListening();
        } catch (Exception e) {
            Toast.makeText(this, "Error starting conversation mode: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            isInConversationMode = false;
            updateMicButtonAppearance();
        }
    }

    private void stopConversationMode() {
        try {
            // First update the state flag to prevent any callbacks from restarting listening
            isInConversationMode = false;
            
            // Cancel the current listening session
            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
                speechRecognizer.cancel();
                
                // Completely destroy and recreate on next use to ensure clean state
                speechRecognizer.destroy();
                speechRecognizer = null;
            }
            
            // Update UI after state is fully cleaned up
            updateMicButtonAppearance();
            
            // Show a toast to indicate that conversation mode is stopped
            Toast.makeText(this, "Conversation mode stopped", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error stopping conversation mode: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Make sure we're in a clean state regardless of errors
            isInConversationMode = false;
            updateMicButtonAppearance();
        }
    }

    private void startListening() {
        try {
            // Don't try to start if we've already stopped
            if (!isInConversationMode || speechRecognizer == null) {
                return;
            }

            Intent intent = getIntent1();

            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error starting voice recognition: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            stopConversationMode();
        }
    }

    @NonNull
    private static Intent getIntent1() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // Add a shorter timeout to prevent hanging
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
        return intent;
    }

    private void updateMicButtonAppearance() {
        try {
            runOnUiThread(() -> {
                try {
                    if (isInConversationMode) {
                        // Make the change very obvious - change size, image and background
                        micButton.setImageResource(android.R.drawable.ic_media_pause);
                        micButton.setBackground(ContextCompat.getDrawable(this, R.drawable.mic_active_background));
                        micButton.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    } else {
                        micButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                        micButton.setBackground(null);
                        micButton.setColorFilter(ContextCompat.getColor(this, R.color.grey_text));
                    }
                } catch (Exception e) {
                    // Silent catch to prevent UI thread crashes
                }
            });
        } catch (Exception e) {
            // Silent catch to prevent crashes
        }
    }

    private void processVoiceInput(String text) {
        // Use Ultravox API to process voice input
        if (TextUtils.isEmpty(text)) {
            return;
        }
        
        // Add user message to chat
        addUserMessage(text);
        
        // Clear the input field
        messageInput.setText("");
        
        // Get the Ultravox repository instance
        com.example.afinal.api.UltravoxRepository repository = com.example.afinal.api.UltravoxRepository.getInstance();
        
        // Show a loading message
        Message typingMessage = new Message("Thinking...", Message.TYPE_ASSISTANT);
        messageAdapter.addMessage(typingMessage);
        scrollToBottom();
        
        // Use streaming API for better user experience
        repository.sendTextRequestStreaming(text, new com.example.afinal.api.UltravoxRepository.StreamingCallback() {
            final StringBuilder responseBuilder = new StringBuilder();
            
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onContent(String content, boolean isDone) {
                responseBuilder.append(content);
                
                // Update assistant message with the latest content
                runOnUiThread(() -> {
                    typingMessage.updateContent(responseBuilder.toString());
                    messageAdapter.notifyDataSetChanged();
                });
            }
            
            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    // Remove the typing message and add the final response
                    messageAdapter.removeMessage(typingMessage);
                    String finalResponse = responseBuilder.toString();
                    addAssistantMessage(finalResponse);
                    
                    // Implement TTS for voice output
                    convertTextToSpeech(finalResponse);
                });
            }
            
            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    // Remove the typing message and add an error message
                    messageAdapter.removeMessage(typingMessage);
                    addAssistantMessage("I'm sorry, I encountered an error: " + error.getMessage());
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Make sure we stop listening when app is paused
        if (isInConversationMode) {
            stopConversationMode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            try {
                speechRecognizer.destroy();
                speechRecognizer = null;
            } catch (Exception e) {
                // Silent catch to prevent crashes during cleanup
            }
        }
        
        // Release media player resources
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void setupImageButton() {
        attachButton.setOnClickListener(v -> showImageSourceChooser());
    }
    
    private void showImageSourceChooser() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_source);
        
        // Get view references
        View cameraOption = dialog.findViewById(R.id.cameraOption);
        View galleryOption = dialog.findViewById(R.id.galleryOption);
        
        // Set click listeners
        cameraOption.setOnClickListener(v -> {
            dialog.dismiss();
            checkCameraPermissionAndTakePhoto();
        });
        
        galleryOption.setOnClickListener(v -> {
            dialog.dismiss();
            checkStoragePermissionAndPickImage();
        });
        
        // Show dialog
        dialog.show();
    }
    
    private void checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
    
    private void openCamera() {
        // Create file for camera image
        cameraImageUri = createImageFile();
        if (cameraImageUri != null) {
            cameraLauncher.launch(cameraImageUri);
        } else {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }
    
    private Uri createImageFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            
            // Get the content:// URI for the image file
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } catch (Exception e) {
            Log.e(TAG, "Error creating image file: " + e.getMessage(), e);
            return null;
        }
    }
    
    private void checkStoragePermissionAndPickImage() {
        // For Android 13+ (API 33+), we need READ_MEDIA_IMAGES
        // For Android 12 and below, we need READ_EXTERNAL_STORAGE
        String permission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? 
                Manifest.permission.READ_MEDIA_IMAGES : 
                Manifest.permission.READ_EXTERNAL_STORAGE;
                
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            storagePermissionLauncher.launch(permission);
        }
    }
    
    private void setupFileButton() {
        fileButton.setOnClickListener(v -> Toast.makeText(this, "File upload functionality coming soon", Toast.LENGTH_SHORT).show());
    }
    
    @SuppressLint("IntentReset")
    private void openImagePicker() {
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    
    private void handleSelectedImage(Uri imageUri) {
        try {
            // Show a toast confirming image selection
            Toast.makeText(this, "Image selected for analysis", Toast.LENGTH_SHORT).show();
            
            // Load and display the image in a dialog
            showImageAnalysisDialog(imageUri);
            
        } catch (Exception e) {
            Toast.makeText(this, "Error handling image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @SuppressLint("SetTextI18n")
    private void showImageAnalysisDialog(Uri imageUri) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_preview);
        
        ImageView previewImage = dialog.findViewById(R.id.previewImage);
        TextView resultText = dialog.findViewById(R.id.analysisResultText);
        Button closeButton = dialog.findViewById(R.id.closeButton);
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        
        // Set the filename as the title
        dialogTitle.setText("Analysis: " + getFileNameFromUri(imageUri));
        
        // Load the image into the ImageView
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            previewImage.setImageBitmap(selectedImage);
        } catch (Exception e) {
            previewImage.setImageResource(android.R.drawable.ic_menu_report_image);
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
        
        // Set initial analyzing state
        resultText.setText("Analyzing image with Ultravox AI...");
        
        // Close button listener
        closeButton.setOnClickListener(v -> dialog.dismiss());
        
        // Show the dialog
        dialog.show();
        
        // Simulate analysis (in a real app, this would call your AI model)
        analyzeImageInDialog(imageUri, resultText);
    }
    
    private String getFileNameFromUri(Uri uri) {
        String result = "";
        if (Objects.equals(uri.getScheme(), "content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    } else {
                        result = uri.getLastPathSegment();
                    }
                }
            } catch (Exception e) {
                result = uri.getLastPathSegment();
            }
        } else {
            result = uri.getLastPathSegment();
        }
        return result != null ? result : "unknown_image";
    }
    
    @SuppressLint("SetTextI18n")
    private void analyzeImageInDialog(Uri imageUri, TextView resultTextView) {
        // Implement image analysis for breast cancer detection using Ultravox
        try {
            // Get the Ultravox repository instance
            com.example.afinal.api.UltravoxRepository repository = com.example.afinal.api.UltravoxRepository.getInstance();
            
            // Load the image as a bitmap
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            
            // Set initial analyzing state
            resultTextView.setText("Analyzing image with Ultravox AI...");
            
            // Use streaming API for better user experience
            repository.sendImageRequestStreaming(bitmap, "Please analyze this medical image for breast cancer detection", 
                new com.example.afinal.api.UltravoxRepository.StreamingCallback() {
                    final StringBuilder responseBuilder = new StringBuilder();
                    
                    @Override
                    public void onContent(String content, boolean isDone) {
                        responseBuilder.append(content);
                        
                        // Update result text with the latest content
                        runOnUiThread(() -> {
                            if (responseBuilder.length() > 0) {
                                resultTextView.setText(responseBuilder.toString());
                            }
                        });
                    }
                    
                    @Override
                    public void onComplete() {
                        // Final response is already displayed
                    }
                    
                    @Override
                    public void onError(Exception error) {
                        runOnUiThread(() -> resultTextView.setText("Error analyzing image: " + error.getMessage()));
                    }
                });
        } catch (Exception e) {
            resultTextView.setText("Error processing image: " + e.getMessage());
        }
    }

    // Set up the test API button
    private void setupTestApiButton() {
        testApiButton.setOnClickListener(v -> testApiConnection());
    }
    
    private void testApiConnection() {
        // Show a loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Testing connection to Ultravox API...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Get the Ultravox repository instance
        com.example.afinal.api.UltravoxRepository repository = com.example.afinal.api.UltravoxRepository.getInstance();
        
        // Make a simple test request
        String testMessage = "Hello, this is a test request to verify API connectivity. Please respond with a short greeting.";
        
        Log.d("UltravoxApiTest", "Sending test request to Fixie API: " + testMessage);
        Log.d("UltravoxApiTest", "Model: " + com.example.afinal.api.UltravoxConfig.MODEL_NAME);
        Log.d("UltravoxApiTest", "API URL: " + com.example.afinal.api.UltravoxConfig.BASE_URL + com.example.afinal.api.UltravoxConfig.TEXT_ENDPOINT);
        
        // Use streaming API for better user experience
        repository.sendTextRequestStreaming(testMessage, new com.example.afinal.api.UltravoxRepository.StreamingCallback() {
            
            final StringBuilder responseBuilder = new StringBuilder();
            
            @Override
            public void onContent(String content, boolean isDone) {
                Log.d("UltravoxApiTest", "Received content: " + content);
                responseBuilder.append(content);
                
                // Update progress dialog with partial response if it's too long
                if (responseBuilder.length() > 30) {
                    String shortPreview = responseBuilder.substring(0, 30) + "...";
                    runOnUiThread(() -> {
                        if (progressDialog.isShowing()) {
                            progressDialog.setMessage("Receiving: " + shortPreview);
                        }
                    });
                }
            }
            
            @Override
            public void onComplete() {
                Log.d("UltravoxApiTest", "Response complete: " + responseBuilder);
                runOnUiThread(() -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showApiTestResult(true, responseBuilder.toString());
                });
            }
            
            @Override
            public void onError(Exception error) {
                Log.e("UltravoxApiTest", "Error during API test", error);
                runOnUiThread(() -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showApiTestResult(false, error.getMessage());
                });
            }
        });
    }
    
    private void showApiTestResult(boolean isSuccess, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        if (isSuccess) {
            builder.setTitle("API Connection Successful");
            builder.setMessage("Response: " + message);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            
            // Add the response to the chat
            addAssistantMessage("API Test Response: " + message);
        } else {
            builder.setTitle("API Connection Failed");
            builder.setMessage("Error: " + message);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            
            // Add setup button if the API key is invalid
            if (message.contains("Invalid API key")) {
                builder.setPositiveButton("Set Up API Key", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, com.example.afinal.api.UltravoxApiKeySetupActivity.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            } else {
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            }
        }
        
        builder.show();
        
        // Log the result for debugging
        String tag = "UltravoxApiTest";
        if (isSuccess) {
            Log.i(tag, "API connection test successful: " + message);
        } else {
            Log.e(tag, "API connection test failed: " + message);
        }
    }

    /**
     * Convert text to speech using Ultravox TTS API
     * @param text Text to convert to speech
     */
    private void convertTextToSpeech(String text) {
        // Only proceed if text is not empty
        if (TextUtils.isEmpty(text)) {
            return;
        }
        
        Log.d(TAG, "Converting text to speech: " + text);
        
        // Get the Ultravox repository instance
        com.example.afinal.api.UltravoxRepository repository = com.example.afinal.api.UltravoxRepository.getInstance();
        
        // Show a toast to indicate speech synthesis is starting
        Toast.makeText(this, "Generating speech...", Toast.LENGTH_SHORT).show();
        
        // Send request to TTS service
        repository.sendVoiceRequest(text, new com.example.afinal.api.UltravoxRepository.ResponseCallback<>() {
            @Override
            public void onSuccess(InputStream audioStream) {
                Log.d(TAG, "Received audio stream response");

                try {
                    // Create a temporary file to store the audio
                    java.io.File tempFile = java.io.File.createTempFile("tts_output", ".mp3", getCacheDir());
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);

                    // Copy audio data to the file
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = audioStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                    audioStream.close();

                    Log.d(TAG, "Audio saved to temp file: " + tempFile.getAbsolutePath());

                    // Play the audio file
                    runOnUiThread(() -> playAudio(tempFile.getAbsolutePath()));

                } catch (Exception e) {
                    Log.e(TAG, "Error processing audio stream: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            "Error playing speech audio", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error in TTS request: " + error.getMessage(), error);
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Error generating speech", Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    /**
     * Play audio from the given file path
     * @param filePath Path to the audio file
     */
    private void playAudio(String filePath) {
        try {
            // Release previous MediaPlayer if exists
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
            
            // Create and configure new MediaPlayer
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            
            // Set data source and prepare
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            
            // Set completion listener to release resources
            mediaPlayer.setOnCompletionListener(mp -> Log.d(TAG, "Audio playback completed"));
            
            // Start playback
            mediaPlayer.start();
            Log.d(TAG, "Audio playback started");
            
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio: " + e.getMessage(), e);
            Toast.makeText(this, "Error playing voice response", Toast.LENGTH_SHORT).show();
        }
    }
}
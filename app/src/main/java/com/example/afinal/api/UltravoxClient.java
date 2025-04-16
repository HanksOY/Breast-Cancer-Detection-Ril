package com.example.afinal.api;

import android.util.Log;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit client for API
 */
public class UltravoxClient {
    
    private static final String TAG = "UltravoxClient";
    private static volatile UltravoxClient instance;
    private final UltravoxService service;
    
    private UltravoxClient() {
        // Create OkHttp client with logging and timeouts
        OkHttpClient okHttpClient = createOkHttpClient();
        
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UltravoxConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        // Create service interface
        service = retrofit.create(UltravoxService.class);
        
        Log.d(TAG, "UltravoxClient initialized with API key: " + 
              UltravoxConfig.API_KEY.substring(0, Math.min(5, UltravoxConfig.API_KEY.length())) + "..." +
              " and base URL: " + UltravoxConfig.BASE_URL);
    }
    
    /**
     * Get singleton instance of UltravoxClient
     */
    public static UltravoxClient getInstance() {
        if (instance == null) {
            synchronized (UltravoxClient.class) {
                if (instance == null) {
                    instance = new UltravoxClient();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get the API service interface
     */
    public UltravoxService getService() {
        return service;
    }
    
    /**
     * Create OkHttpClient with logging and timeouts
     */
    private OkHttpClient createOkHttpClient() {
        // Create logging interceptor with BODY level for complete API debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            Log.d(TAG, "API: " + message);
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Build OkHttp client with timeouts and logging
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(UltravoxConfig.TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(UltravoxConfig.TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(UltravoxConfig.TIMEOUT_WRITE, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    // Log the request before it's sent
                    okhttp3.Request request = chain.request();
                    Log.d(TAG, "Sending request to: " + request.url());
                    Log.d(TAG, "Headers: " + request.headers());
                    
                    // Proceed with the request
                    okhttp3.Response response = chain.proceed(request);
                    
                    // Log response status
                    Log.d(TAG, "Received response: " + response.code() + " " + response.message());
                    
                    return response;
                })
                .build();
                
        return client;
    }
    
    /**
     * Format API key for authentication header
     */
    public static String formatAuthHeader(String apiKey) {
        // Check if API key is valid
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Log.e(TAG, "API key is null or empty!");
        } else {
            Log.d(TAG, "Formatting API key: " + apiKey.substring(0, Math.min(5, apiKey.length())) + "...");
        }
        
        // Return the proper authentication header for Anthropic API
        return "Bearer " + apiKey;
    }
} 
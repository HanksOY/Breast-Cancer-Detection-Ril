package com.example.afinal.api;

import com.example.afinal.api.models.ImageRequest;
import com.example.afinal.api.models.TextRequest;
import com.example.afinal.api.models.UltravoxResponse;
import com.example.afinal.api.models.VoiceRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * Retrofit service interface for API
 */
public interface UltravoxService {
    
    /**
     * Send a text request to API
     * @param authHeader Authorization header with API key
     * @param contentType Content type header
     * @param request Text request body
     * @return Call object for UltravoxResponse
     */
    @POST(UltravoxConfig.TEXT_ENDPOINT)
    Call<UltravoxResponse> sendTextRequest(
        @Header("anthropic-version") String anthropicVersion,
        @Header("Authorization") String authHeader,
        @Header(UltravoxConfig.HEADER_CONTENT_TYPE) String contentType,
        @Body TextRequest request
    );
    
    /**
     * Send a text request to API with streaming response
     * @param authHeader API key in header format
     * @param contentType Content type header 
     * @param request Text request body
     * @return Call object for ResponseBody (for streaming)
     */
    @Streaming
    @POST(UltravoxConfig.TEXT_ENDPOINT + "?stream=true")
    Call<ResponseBody> sendTextRequestStreaming(
        @Header("anthropic-version") String anthropicVersion,
        @Header("Authorization") String authHeader,
        @Header(UltravoxConfig.HEADER_CONTENT_TYPE) String contentType,
        @Body TextRequest request
    );

    /**
     * Send an image request with streaming response
     * @param authHeader API key in header format
     * @param contentType Content type header
     * @param request Image request body
     * @return Call object for ResponseBody (for streaming)
     */
    @Streaming
    @POST(UltravoxConfig.IMAGE_ENDPOINT + "?stream=true")
    Call<ResponseBody> sendImageRequestStreaming(
        @Header("anthropic-version") String anthropicVersion,
        @Header("Authorization") String authHeader,
        @Header(UltravoxConfig.HEADER_CONTENT_TYPE) String contentType,
        @Body ImageRequest request
    );

    /**
     * Send a voice request for TTS
     * @param authHeader API key in header format
     * @param contentType Content type header
     * @param request Voice request body
     * @return Call object for ResponseBody (audio stream)
     */
    @Streaming
    @POST(UltravoxConfig.AUDIO_ENDPOINT)
    Call<ResponseBody> sendVoiceRequest(
        @Header("anthropic-version") String anthropicVersion,
        @Header("Authorization") String authHeader,
        @Header(UltravoxConfig.HEADER_CONTENT_TYPE) String contentType,
        @Body VoiceRequest request
    );
} 
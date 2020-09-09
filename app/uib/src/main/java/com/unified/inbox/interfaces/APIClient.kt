package com.unified.inbox.interfaces

import com.google.gson.JsonObject
import com.unified.inbox.beans.ChatHistoryResponse
import com.unified.inbox.beans.PostMessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

internal interface APIClient {

    @POST("app/{appId}/bot/{botId}/user/{userId}/messages")
    fun sendMessageRequest(
        @Path("appId") appId: String,
        @Path("botId") botId: String,
        @Path("userId") userId: String,
        @Body message: JsonObject
    ): Call<PostMessageResponse>?


    //@Multipart
    @POST("app/{appId}/bot/{botId}/user/{userId}/messages")
    fun sendImageMessageRequest(
        @Path("appId") appId: String,
        @Path("botId") botId: String,
        @Path("userId") userId: String,
        @Body type: RequestBody
    ): Call<PostMessageResponse>?

    //@Multipart
    @POST("app/{appId}/bot/{botId}/user/{userId}/messages")
    fun sendAudioUploadRequest(
        @Path("appId") appId: String,
        @Path("botId") botId: String,
        @Path("userId") userId: String,
        @Body type: RequestBody
    ): Call<PostMessageResponse>?


    @GET("user/chatbot_messages/app/{appId}/bot/{botId}/user/{userId}")
    fun getChats(
        @Header("Authorization") token: String,
        @Path("appId") appId: String,
        @Path("botId") botId: String,
        @Path("userId") userId: String,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): Call<ChatHistoryResponse>?
}
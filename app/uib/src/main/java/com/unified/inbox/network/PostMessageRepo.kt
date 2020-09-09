package com.unified.inbox.network

import com.google.gson.JsonObject
import com.unified.inbox.BuildConfig
import com.unified.inbox.beans.PostMessageResponse
import com.unified.inbox.interfaces.APIClient
import com.unified.inbox.interfaces.OnResponseListener
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class PostMessageRepo(private var responseListener: OnResponseListener) {
    private var apiClient: APIClient? = null

    //production
    private val URL = "https://chatbot-v2connector.unificationengine.com/"
    //beta
    // private val URL = "https://beta-chatbot-v2connector.unificationengine.com/"

    init {
        apiClient =
            RetrofitInstance()
                .getRetrofitInstance(BuildConfig.HOST)
                .create(APIClient::class.java)
    }

    fun postMessage(
        message: JsonObject,
        appId: String,
        botId: String,
        userId: String,
        adapterPosition: Int
    ) {

        val call: Call<PostMessageResponse>? = apiClient?.sendMessageRequest(
            message = message,
            appId = appId,
            botId = botId,
            userId = userId
        )
        call?.enqueue(object : Callback<PostMessageResponse> {
            override fun onResponse(
                call: Call<PostMessageResponse>,
                response: Response<PostMessageResponse>
            ) {
                responseListener.onSuccess(response, adapterPosition)
            }

            override fun onFailure(call: Call<PostMessageResponse>, t: Throwable) {
                responseListener.onError(t, adapterPosition)
            }
        })

    }

    fun postImageMessage(
        part: RequestBody,
        appId: String,
        botId: String,
        userId: String,
        type: String,
        adapterPosition: Int
    ) {

        val call: Call<PostMessageResponse>? = apiClient?.sendImageMessageRequest(
            appId = appId,
            botId = botId,
            userId = userId,type = part

        )
        call?.enqueue(object : Callback<PostMessageResponse> {
            override fun onResponse(
                call: Call<PostMessageResponse>,
                response: Response<PostMessageResponse>
            ) {
                responseListener.onSuccess(response, adapterPosition)
            }

            override fun onFailure(call: Call<PostMessageResponse>, t: Throwable) {
                responseListener.onError(t, adapterPosition)
            }
        })

    }


    fun postAudioMessage(
        type:RequestBody,
        appId: String,
        botId: String,
        userId: String,
        adapterPosition: Int
    ) {

        val call: Call<PostMessageResponse>? = apiClient?.sendAudioUploadRequest(
            type = type,
            appId = appId,
            botId = botId,
            userId = userId

        )
        call?.enqueue(object : Callback<PostMessageResponse> {
            override fun onResponse(
                call: Call<PostMessageResponse>,
                response: Response<PostMessageResponse>
            ) {
                responseListener.onSuccess(response, adapterPosition)
            }

            override fun onFailure(call: Call<PostMessageResponse>, t: Throwable) {
                responseListener.onError(t, adapterPosition)
            }
        })

    }



}
package com.unified.inbox.network

import com.unified.inbox.BuildConfig
import com.unified.inbox.beans.ChatHistoryResponse
import com.unified.inbox.interfaces.APIClient
import com.unified.inbox.interfaces.OnResponseListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class ChatHistoryRepo(private var responseListener: OnResponseListener) {
    private var apiClient: APIClient? = null

    //production
    //private val URL = "https://chatbot-v2connector.unificationengine.com/"
    //beta
    //private val URL = "https://beta-unifiedai-api.unificationengine.com/user/chatbot_messages/"

    init {
        apiClient =
            RetrofitInstance()
                .getRetrofitInstance(BuildConfig.CHAT_HISTORY_HOST)
                .create(APIClient::class.java)
    }


    fun getChats(authToken: String, appId: String, botId: String, userId: String,skip: Int,limit: Int) {
        val call: Call<ChatHistoryResponse>? = apiClient?.getChats(
            appId = appId,
            botId = botId,
            userId = userId, token = "Basic $authToken", skip = skip, limit = limit
        )

        call?.enqueue(object : Callback<ChatHistoryResponse> {
            override fun onResponse(
                call: Call<ChatHistoryResponse>,
                response: Response<ChatHistoryResponse>
            ) {
                responseListener.onSuccess(response.body(), 0)
            }

            override fun onFailure(call: Call<ChatHistoryResponse>, t: Throwable) {
                responseListener.onError(t, 0)
            }
        })

    }
}
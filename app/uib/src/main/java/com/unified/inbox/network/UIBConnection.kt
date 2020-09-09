package com.unified.inbox.network

import android.util.Log
import com.star_zero.sse.EventHandler
import com.star_zero.sse.EventSource
import com.star_zero.sse.MessageEvent
import com.unified.inbox.BuildConfig
import com.unified.inbox.interfaces.UIBEventListener
import org.json.JSONObject


internal class UIBConnection(private var uibEventListener: UIBEventListener) {

    private var eventSource: EventSource? = null
    fun connect(appId: String, botId: String, userId: String) {
        //Log.d("host","${BuildConfig.HOST}/app/${appId}/bot/${botId}/user/${userId}/notification")
        //Log.d("host","https://beta-chatbot-v2connector.unificationengine.com/app/${appId}/bot/${botId}/user/${userId}/notification")
        eventSource = EventSource(
            // the appid to be changed is c9e0faed-0d1c-4580-a118-ad017811688a
            //The chatbot connector is deployed to live the url is
            //https://chatbot-v2connector.unificationengine.com
            //
            //
            //
            //Alos change messages api
            //https://chatbot-v2connector.unificationengine.com/app/c9e0faed-0d1c-4580-a118-ad017811688a/bot/123456/user/5e71ba3ba4c44a0e9f60854e/messages
            //production
            //"https://chatbot-v2connector.unificationengine.com/app/${appId}/bot/${botId}/user/${userId}/notification",
            //beta
            //"https://beta-chatbot-v2connector.unificationengine.com/app/${appId}/bot/${botId}/user/${userId}/notification",
            "${BuildConfig.HOST}app/${appId}/bot/${botId}/user/${userId}/notification",
            object : EventHandler {
                override fun onOpen() {
                    Log.d("message", "OPEN")
                }

                override fun onMessage(event: MessageEvent) {
                    Log.d("message", event.data)
                    Log.d("message", event.event!!)
                    if (!event.event.equals("question")
                        &&!event.event.equals("question_image_link")
                        &&!event.event.equals("question_audio_link")
                        &&!event.event.equals("question_document")
                    ) {

                     /*val data = if(event.data.contains('\"') )  {
                            val jsonObject=JSONObject(event.data.replace('\"','"'))
                            jsonObject.getString("text")
                        }else {
                            event.data
                        }*/
                        uibEventListener.onEventResponse(event.data, event.event!!)
                    }
                }

                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                    Log.e("message ${e?.message}", e.toString())
                    uibEventListener.onEventError(e!!)

                }

            }
        )
        eventSource?.connect()
    }


    fun disposeEventSource() {
        eventSource?.close()
    }
}
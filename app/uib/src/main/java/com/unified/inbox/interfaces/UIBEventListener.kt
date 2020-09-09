package com.unified.inbox.interfaces

internal interface UIBEventListener {

    fun <T> onEventResponse(response: T,type:String)
    fun onEventError(error: Exception)
}
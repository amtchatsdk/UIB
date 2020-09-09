package com.unified.inbox.interfaces

internal interface OnResponseListener {

    fun <T> onSuccess(response: T, adapterPosition: Int)
    fun onError(error: Throwable, adapterPosition: Int)
}
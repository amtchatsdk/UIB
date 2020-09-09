package com.unified.inbox.beans


import com.google.gson.annotations.SerializedName


data class PostMessageResponse(

    @field:SerializedName("status")
    val status: Int? = null
)
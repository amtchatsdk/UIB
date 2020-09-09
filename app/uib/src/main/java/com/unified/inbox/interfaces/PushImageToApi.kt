package com.unified.inbox.interfaces

import android.graphics.Bitmap
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import java.io.File

interface PushImageToApi {
    fun apiCallForImage(part: RequestBody,bitmap: Bitmap)
    fun sendUIBDocument(part: RequestBody,document: File,fileName: String,filePath: String)
}
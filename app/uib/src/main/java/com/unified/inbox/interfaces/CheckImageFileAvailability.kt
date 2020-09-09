package com.unified.inbox.interfaces

import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

interface CheckImageFileAvailability {
    fun fileImageIsReadyForUpload(file: File,imageSizeInMB:Double)
    fun createPartBody(file: File, type: String): RequestBody
}
package com.unified.inbox.beans

import android.graphics.Bitmap
import java.io.File

/*
data class Chat(var msg: String,val bitmap: Bitmap, var from: Int)*/
class Chat {
    var msg: String? = null
    var oldMsg: String? = null
    var isEdited: Boolean? = false
    var repliedAtTimeStamp: String? = null
    var msgID: String? = null
    var bitmap: Bitmap? = null
    var from: Int? = null
    var type: String? = null
    var audioFile: File? = null
    var document: File? = null
    var sentStatus: Int? = null
    var position: Int? = null
    var fileName: String? = null
    var contactName: String? = null
    var uibContactObject:UIBContactObject?=null
}

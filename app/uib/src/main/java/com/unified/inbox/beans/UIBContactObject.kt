package com.unified.inbox.beans

import android.graphics.Bitmap

class UIBContactObject {
    var contactName: String? = null
    var phoneNumber: String? = null
    var contactPhoto: Bitmap? = null
    override fun toString(): String {
        return "UIBContactObject(contactName=$contactName, phoneNumber=$phoneNumber,contactPhoto=$contactPhoto)"
    }
}
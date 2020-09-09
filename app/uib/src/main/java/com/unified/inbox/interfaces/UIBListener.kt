package com.unified.inbox.interfaces

import android.graphics.drawable.Drawable
import com.unified.inbox.beans.UIBAttachmentMenu

internal interface UIBListener {
    //fun sendMessage(message: String)

    fun setBackGroundForLayout(background: Drawable)
    fun setUserMessageTextSize(size: Float)
    fun setUserMessageTextStyle(style: Int)
    fun setUserMessageTextColor(color: Int)
    fun setUserMessageTextBackground(background: Drawable)
    fun setSupportMessageTextStyle(style: Int)
    fun setSupportMessageTextColor(color: Int)
    fun setSupportMessageTextSize(size: Float)
    fun setSupportMessageTextBackground(background: Drawable)
    //fun setNoDataTextVisibility(visibility: Int)
    //fun setNoDataText(value: String, textColor: Int, textStyle: Int, textSize: Float)
    fun setMarginForUserText(adjustMargin: Boolean, left: Int, right: Int, top: Int, bottom: Int)
    fun setPaddingForUserText(adjustPadding: Boolean, left: Int, right: Int, top: Int, bottom: Int)
    fun setMarginForSupportText(adjustMargin: Boolean, left: Int, right: Int, top: Int, bottom: Int)
    fun setPaddingForSupportText(
        adjustPadding: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    )

    //fun setAccountDetails(appId: String, botId: String, userId: String)

    fun getAuth(appId: String, botId: String, userId: String): String

    fun getAttachmentMenu(): ArrayList<UIBAttachmentMenu>

    fun setUIBAttachmentMenu(
        camera: Boolean,
        gallery: Boolean,
        document: Boolean,
        audio: Boolean/*,
        location: Boolean,
        contact: Boolean*/
    )
}
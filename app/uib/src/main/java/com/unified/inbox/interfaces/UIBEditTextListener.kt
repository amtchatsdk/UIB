package com.unified.inbox.interfaces

import android.graphics.drawable.Drawable
import com.unified.inbox.ui.UIBChatFragment

internal interface UIBEditTextListener {
    fun setUIBSendIcon(icon: Drawable): UIBChatFragment
    fun setUIBSendButtonBackground(background: Drawable): UIBChatFragment
    fun setUIBEditTextHint(hint: String): UIBChatFragment
    fun setUIBEditTextBackground(background: Drawable): UIBChatFragment
    fun setUIBEditTextInputType(inputType: Int): UIBChatFragment
    fun setPaddingForEditText(adjustPadding: Boolean, left: Int, right: Int, top: Int, bottom: Int): UIBChatFragment
    /*fun setPaddingForEditTextContainer(
        adjustPadding: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    )*/

    fun setMarginForEditText(adjustMargin: Boolean, left: Int, right: Int, top: Int, bottom: Int): UIBChatFragment
    /*fun setMarginForEditTextContainer(
        adjustMargin: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    )*/

    fun showErrorMessageForEmptyValue(errorString: String): UIBChatFragment
    fun shouldShowErrorForEmptyMessage(value: Boolean): UIBChatFragment
    fun trimTypedMessage(value: Boolean): UIBChatFragment
    fun setUIBEditTextHintColor(color: Int): UIBChatFragment
}
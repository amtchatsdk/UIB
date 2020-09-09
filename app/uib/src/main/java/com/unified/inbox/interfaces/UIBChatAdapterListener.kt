package com.unified.inbox.interfaces

import android.graphics.drawable.Drawable
import com.unified.inbox.adapters.UIBChatAdapter

internal interface UIBChatAdapterListener {

    fun setUserMessageTextSize(size: Float)
    fun setUserMessageTextStyle(style: Int)
    fun setUserRepliedMessageTextStyle(style: Int)
    fun setBotMessageTextSize(size: Float)
    fun setBotMessageTextStyle(style: Int)
    fun setUserMessageTextColor(color: Int)
    fun setBotMessageTextColor(color: Int)
    fun setUserMessageTextBackground(background: Drawable)
    fun setSupportMessageTextBackground(background: Drawable)
    fun bindUserMessage(holder: UIBChatAdapter.UserMessageViewHolder, position: Int)
    fun bindUserMessageReply(holder: UIBChatAdapter.UserMessageReplyViewHolder, position: Int)
    fun bindBotMessage(holder: UIBChatAdapter.BotMessageViewHolder, position: Int)
    fun bindBotImage(holder: UIBChatAdapter.BotImageViewHolder, position: Int)
    fun bindUserImageMessage(holder: UIBChatAdapter.UserImageViewHolder, position: Int)
    fun bindUserAudioMessage(holder: UIBChatAdapter.UserAudioViewHolder, position: Int)
    fun bindUserDocumentMessage(holder: UIBChatAdapter.UserFileViewHolder, position: Int)
    fun bindUserContact(holder: UIBChatAdapter.UserContactViewHolder, position: Int)
    fun setMarginForUserText(adjustMargin: Boolean, left: Int, right: Int, top: Int, bottom: Int)
    fun setPaddingForUserText(adjustPadding: Boolean, left: Int, right: Int, top: Int, bottom: Int)
    fun setMarginForBotText(adjustMargin: Boolean, left: Int, right: Int, top: Int, bottom: Int)
    fun setPaddingForBotText(adjustPadding: Boolean, left: Int, right: Int, top: Int, bottom: Int)
}
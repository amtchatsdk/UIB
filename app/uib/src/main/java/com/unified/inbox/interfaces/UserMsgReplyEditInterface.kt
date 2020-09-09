package com.unified.inbox.interfaces

interface UserMsgReplyEditInterface {
    fun userIsEditing(userIsEditing:Boolean)
    fun userIsReplying(userIsReplying:Boolean)
    fun userIsSendingNewOne(userIsSendingNewMsg:Boolean)
}
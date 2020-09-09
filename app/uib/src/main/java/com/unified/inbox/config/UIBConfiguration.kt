package com.unified.inbox.config

import com.unified.inbox.exception.UIBException

class UIBConfiguration {
    private var mAppId: String? = null
    private var mBotId: String? = null

    fun setAppId(appId: String): UIBConfiguration {
        this.mAppId = appId
        return this
    }

    fun setBotId(botId: String): UIBConfiguration {
        this.mBotId = botId
        return this
    }

    fun init() {
        if (mAppId != null && mAppId != "") {

        } else {
            throw UIBException("App Id required")
        }
    }
}
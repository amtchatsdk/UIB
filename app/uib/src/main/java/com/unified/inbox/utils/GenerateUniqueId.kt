package com.unified.inbox.utils

import java.util.*

class GenerateUniqueId {

    companion object{
        fun getUniqueMessageId():String{
           return UUID.randomUUID().toString()
        }
    }

}
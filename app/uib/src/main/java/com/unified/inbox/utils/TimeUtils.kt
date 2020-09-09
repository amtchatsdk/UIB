package com.unified.inbox.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {

    companion object{
        @SuppressLint("SimpleDateFormat")
        fun getCurrentTime(): String? {
            val df: DateFormat = SimpleDateFormat("hh:mm a") // Format time

            Log.d("Reply At",df.format(Calendar.getInstance().time))
            return df.format(Calendar.getInstance().time)
        }
    }
}
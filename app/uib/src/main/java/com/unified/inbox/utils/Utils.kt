package com.unified.inbox.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboardByView() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun Fragment.isCurrentTimeInBetweenSlots(
    startTime: String?,
    endTime: String?,
    timeZone: String
): Boolean {
    var value = false
    try {
        val sdfStartTIme = SimpleDateFormat("HH.mm")!!
        val calendar1 = Calendar.getInstance()
        //int date = calendar1.get(Calendar.DAY_OF_MONTH);
        val time0 = sdfStartTIme.parse(startTime!!)
        //sdfStartTIme.setTimeZone(TimeZone.getTimeZone("UTC"));
        val time1 = sdfStartTIme.parse(startTime)
        if (time0?.date!! < time1?.date!!) {
            time1.date = time0.date
        }
        calendar1.time = time1
        calendar1.timeZone = TimeZone.getTimeZone(timeZone)
        //calendar1.set(Calendar.DAY_OF_MONTH, date);
        //Log.d("dayOfWeek", calendar1.get(Calendar.DAY_OF_WEEK) + "");
        //calendar1.add(Calendar.HOUR, 25);
        //Log.d("AddedTime", calendar1.getTime() + "");
        // Log.d("dayOfWeek", calendar1.get(Calendar.DAY_OF_WEEK) + "");
        val sdfEndTime = SimpleDateFormat("HH.mm")!!
        val calendar2 = Calendar.getInstance()
        val time01 = sdfEndTime.parse(endTime!!)
        //sdfEndTime.setTimeZone(TimeZone.getTimeZone("UTC"));
        val time2 = sdfEndTime.parse(endTime)
        if (time2?.date!! < time01?.date!!) {
            time2.date = time01.date
        }
        calendar2.time = time2
        //Log.d("AddedTime", calendar2.getTime() + "");
        calendar2.timeZone = TimeZone.getTimeZone(timeZone)
        //Log.d("AddedTime", calendar2.getTime() + "");
        val sdf = SimpleDateFormat("HH.mm")!!
        val str = sdf.format(Date())
        val currentTime = sdf.parse(str)
        val calendar3 = Calendar.getInstance()
        calendar3.time = currentTime!!
        calendar3.timeZone = TimeZone.getTimeZone(timeZone)
        //calendar3.add(Calendar.DATE, 1);

        val x = calendar3.time
        //val y = calendar4.time
        /*Log.d("thisMenuTimes", x + " x");
            Log.d("thisMenuTimes", y + " y");
            Log.d("thisMenuTimes", calendar1.getTime() + " s");
            Log.d("thisMenuTimes", calendar2.getTime() + " e");*/

        //if (x.after(calendar1.getTime()) && x.before(calendar2.getTime()) || (x.before(calendar1.getTime()))) {
        value = if (!calendar1.time.after(x) && !calendar2.time.before(x)) {
            //checkes whether the current time is between 14:49:00 and 20:11:13.
            println(true)
            true
        } else {
            false
        }
    } catch (e: ParseException) {
        value = false
        e.printStackTrace()
    }
    return value
}



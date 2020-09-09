package com.unified.inbox.beans

import android.os.Parcel
import android.os.Parcelable

class UIBConfig(
    var appId: String,
    var botId: String,
    var userId: String? = null,
    var botName: String,
    var botIcon: String,
    var chatPreview: Boolean? = false,
   // var chatWorkingHours: WorkingHours,
    var inactiveChatPreviewMsg: String? = null,
    var activeChatPreviewMsg: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
       // parcel.readParcelable(WorkingHours::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(appId)
        dest?.writeString(botId)
        dest?.writeString(userId)
        dest?.writeString(botName)
        dest?.writeString(botIcon)
        dest?.writeString(inactiveChatPreviewMsg)
        //dest?.writeParcelable(chatWorkingHours, flags)
        dest?.writeByte(if (chatPreview!!) 1 else 0)
        dest?.writeString(activeChatPreviewMsg)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UIBConfig> {
        override fun createFromParcel(parcel: Parcel): UIBConfig {
            return UIBConfig(parcel)
        }

        override fun newArray(size: Int): Array<UIBConfig?> {
            return arrayOfNulls(size)
        }
    }
}


data class WorkingHours(
    var workingTimeStatus: Boolean,
    var timeZone: String,
    var workingTime: List<WorkingTime>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.createTypedArrayList(WorkingTime)!!
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeByte(if (workingTimeStatus) 1 else 0)
        dest?.writeTypedList(workingTime)
        dest?.writeString(timeZone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkingHours> {
        override fun createFromParcel(parcel: Parcel): WorkingHours {
            return WorkingHours(parcel)
        }

        override fun newArray(size: Int): Array<WorkingHours?> {
            return arrayOfNulls(size)
        }
    }
}

data class WorkingTime(var dayName: String, val workingStatus: Boolean, var time: StartEndTime?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(StartEndTime::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dayName)
        parcel.writeByte(if (workingStatus) 1 else 0)
        parcel.writeParcelable(time, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkingTime> {
        override fun createFromParcel(parcel: Parcel): WorkingTime {
            return WorkingTime(parcel)
        }

        override fun newArray(size: Int): Array<WorkingTime?> {
            return arrayOfNulls(size)
        }
    }
}

data class StartEndTime(var startTime: String, var endTime: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(startTime)
        dest?.writeString(endTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StartEndTime> {
        override fun createFromParcel(parcel: Parcel): StartEndTime {
            return StartEndTime(parcel)
        }

        override fun newArray(size: Int): Array<StartEndTime?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.bboba

import android.os.Parcel
import android.os.Parcelable

class Prints_Request(
    val name: String, val id: String, val total_page: String,
    val detail_request: String, val date: String, val time: String,
    val locationx: String, val locationy: String, val location_name: String,
    val per_page: String, val print_fb: String, val print_color: String,
    val picture_location: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(id)
        writeString(total_page)
        writeString(detail_request)
        writeString(date)
        writeString(time)
        writeString(locationx)
        writeString(locationy)
        writeString(location_name)
        writeString(per_page)
        writeString(print_fb)
        writeString(print_color)
        writeString(picture_location)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Prints_Request> =
            object : Parcelable.Creator<Prints_Request> {
                override fun createFromParcel(source: Parcel): Prints_Request =
                    Prints_Request(source)

                override fun newArray(size: Int): Array<Prints_Request?> = arrayOfNulls(size)
            }
    }
}
package com.salva.grainchainchallenge.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ruta")
data class RouteModel(
    var nameRoute: String,
    var distanceRoute: Double = 0.0,
    var timeRoute: String
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString()
    ) {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nameRoute)
        parcel.writeDouble(distanceRoute)
        parcel.writeString(timeRoute)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RouteModel> {
        override fun createFromParcel(parcel: Parcel): RouteModel {
            return RouteModel(parcel)
        }

        override fun newArray(size: Int): Array<RouteModel?> {
            return arrayOfNulls(size)
        }
    }
}

package com.aleksejantonov.tajikair.ui.main

import android.os.Parcel
import android.os.Parcelable
import com.aleksejantonov.tajikair.api.entity.City
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

class LocationSuggestion(val city: City?) : SearchSuggestion {

    constructor(parcel: Parcel) : this(parcel.readParcelable<City>(City::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(city, flags)
    }

    override fun describeContents(): Int = 0

    override fun getBody(): String = city?.fullName ?: ""

    companion object CREATOR : Parcelable.Creator<LocationSuggestion> {
        override fun createFromParcel(parcel: Parcel): LocationSuggestion {
            return LocationSuggestion(parcel)
        }

        override fun newArray(size: Int): Array<LocationSuggestion?> {
            return arrayOfNulls(size)
        }
    }
}
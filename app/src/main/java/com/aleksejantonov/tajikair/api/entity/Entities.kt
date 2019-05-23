package com.aleksejantonov.tajikair.api.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CitiesResponse(@SerializedName("cities") val cities: List<City>)

data class City(
    @SerializedName("countryCode") val code: String,
    @SerializedName("latinFullName") val fullName: String,
    @SerializedName("location") val latLng: LatLng?,
    @SerializedName("iata") val iata: List<String>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(LatLng::class.java.classLoader),
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(fullName)
        parcel.writeParcelable(latLng, flags)
        parcel.writeStringList(iata)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<City> {
        override fun createFromParcel(parcel: Parcel): City = City(parcel)
        override fun newArray(size: Int): Array<City?> = arrayOfNulls(size)
    }
}

data class LatLng(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<LatLng> {
        override fun createFromParcel(parcel: Parcel): LatLng = LatLng(parcel)
        override fun newArray(size: Int): Array<LatLng?> = arrayOfNulls(size)
    }
}
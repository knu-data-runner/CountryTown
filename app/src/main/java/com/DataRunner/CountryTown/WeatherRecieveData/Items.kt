package com.DataRunner.CountryTown.WeatherRecieveData

import android.content.ClipData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Items {
    @SerializedName("item")
    @Expose
    var item: List<Item>? = null

}
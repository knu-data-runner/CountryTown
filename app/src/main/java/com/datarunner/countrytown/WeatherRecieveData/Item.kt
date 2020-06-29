package com.datarunner.countrytown.WeatherRecieveData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Item {
    @SerializedName("baseDate")
    @Expose
    var baseDate: String? = null

    @SerializedName("baseTime")
    @Expose
    var baseTime: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("nx")
    @Expose
    var nx: Int? = null

    @SerializedName("ny")
    @Expose
    var ny: Int? = null

    @SerializedName("obsrValue")
    @Expose
    var obsrValue: String? = null

}
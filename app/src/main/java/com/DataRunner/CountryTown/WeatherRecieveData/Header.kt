package com.DataRunner.CountryTown.WeatherRecieveData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Header {
    @SerializedName("resultCode")
    @Expose
    var resultCode: String? = null

    @SerializedName("resultMsg")
    @Expose
    var resultMsg: String? = null

}
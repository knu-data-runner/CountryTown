package com.DataRunner.CountryTown.WeatherRecieveData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Result {
    @SerializedName("response")
    @Expose
    var response: Response? = null

}
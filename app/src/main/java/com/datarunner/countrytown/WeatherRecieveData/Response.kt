package com.datarunner.countrytown.WeatherRecieveData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Response {
    @SerializedName("header")
    @Expose
    var header: Header? = null

    @SerializedName("body")
    @Expose
    var body: Body? = null

}
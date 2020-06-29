package com.datarunner.countrytown.WeatherRecieveData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Body {
    @SerializedName("dataType")
    @Expose
    var dataType: String? = null

    @SerializedName("items")
    @Expose
    var items: Items? = null

    @SerializedName("pageNo")
    @Expose
    var pageNo: Int? = null

    @SerializedName("numOfRows")
    @Expose
    var numOfRows: Int? = null

    @SerializedName("totalCount")
    @Expose
    var totalCount: Int? = null

}
package com.DataRunner.CountryTown.WeatherRecieveData

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("1360000/VilageFcstInfoService/getUltraSrtNcst")
    fun getResponse(
        @Query("serviceKey", encoded=true) serviceKey: String,
        @Query("base_date") base_date: String,
        @Query("base_time") base_time: String,
        @Query("nx") nx: String,
        @Query("ny") ny: String,
        @Query("pageNo") pageNo: String = "1",
        @Query("numOfRows") numOfRows: String = "20",
        @Query("dataType") dataType: String = "json"
    ): Call<Result>
}
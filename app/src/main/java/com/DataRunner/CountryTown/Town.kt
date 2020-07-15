package com.DataRunner.CountryTown

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Town(
    val title: String,
    val sido: String,
    val sigungu: String,
    val programType: String,
    val programContent: String,
    val addr: String,
    val master: String,
    val number: String,
    val link: String,
    val manage: String,
    val lat: Double,
    val lon: Double,
    val dataVersion: String,
    val townId: String
) : Parcelable
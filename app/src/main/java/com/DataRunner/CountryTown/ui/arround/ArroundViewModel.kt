package com.DataRunner.CountryTown.ui.arround

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArroundViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is arround Fragment"
    }
    val text: LiveData<String> = _text
}
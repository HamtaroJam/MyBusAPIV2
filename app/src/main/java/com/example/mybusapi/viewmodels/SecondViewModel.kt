package com.example.mybusapi.viewmodels

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.retrofit.RetrofitInstance

class SecondViewModel: ViewModel() {

    val _busService = MutableLiveData<String>()
    val busService : LiveData<String>
        get() = _busService

    init {
        _busService.value = ""
    }

}
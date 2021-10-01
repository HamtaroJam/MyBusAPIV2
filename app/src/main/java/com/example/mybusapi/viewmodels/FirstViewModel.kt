package com.example.mybusapi.viewmodels

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.util.Log
import android.view.View
import android.view.ViewManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import com.example.mybusapi.MainActivity
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.fragments.FirstFragment
import com.example.mybusapi.retrofit.RetrofitInstance
import com.example.mybusapi.retrofit.method.bus.BusMarker
import com.example.mybusapi.retrofit.method.busstop.GetALL
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.time.Duration
import kotlin.concurrent.timer

class FirstViewModel(repository : Repository) : ViewModel() {

}
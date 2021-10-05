package com.example.mybusapi.viewmodels

import android.app.Activity
import android.app.Application
import android.app.PendingIntent.getActivity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mybusapi.MainActivity
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.fragments.FirstFragment
import com.example.mybusapi.retrofit.RetrofitInstance
import com.example.mybusapi.retrofit.method.bus.BusMarker
import com.example.mybusapi.retrofit.method.bus.GetRTBUS
import com.example.mybusapi.retrofit.method.busstop.GetALL
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.time.Duration
import kotlin.concurrent.timer

class FirstViewModel(repository : Repository) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO){
            getAllBusStop()
            delay(2000)
            getAllBusLive()
            delay(33000)
            withContext(Dispatchers.Main) {
                findNavController(Repository.firstFragmentShared?:return@withContext).navigate(R.id.action_firstFragment_to_SecondFragment)
            }
        }
    }

    private fun getAllBusStop(){
        for (i in 0..10) {
            RetrofitInstance.api.getPost(i * 500).enqueue(object : Callback<GetALL> {
                override fun onResponse(call: Call<GetALL>, response: Response<GetALL>) {
                    Log.i("0", i.toString())
                    if (!response.isSuccessful) {
                        Log.i("Failed at Bus Stop onResponse: ", response.message())
                        //Toast.makeText(activity, "Unsuccessful Network Call!", Toast.LENGTH_LONG).show()
                        return
                    }
                    val body = response.body()
                    val sizeOfStop = body?.value
                    for (x in sizeOfStop?.indices?:return) {
                        var busStopCode = body.value.get(x).BusStopCode
                        var busStopDescription = body.value.get(x).Description
                        var busStopRoad = body.value.get(x).RoadName
                        Repository.busMarkerList += BusMarker(
                            body.value.get(x).BusStopCode,
                            LatLng(
                                body.value[x].Latitude,
                                body.value[x].Longitude
                            ),
                            body.value[x].Description,
                            body.value[x].RoadName,
                            "Bus Stop Number: $busStopCode ::Bus Stop Station Name: $busStopDescription :::Bus Stop Road: $busStopRoad"
                        )
                    }
                }
                override fun onFailure(call: Call<GetALL>, t: Throwable) {
                    Log.i(call.toString(), t.message?:"Null")
                }
            })
        }
    }

    private fun getAllBusLive(){
        for (x in Repository.busMarkerList.indices) {
            RetrofitInstance.api.getBus(Repository.busMarkerList.get(x).busStopCode.toInt())
                .enqueue(object : Callback<GetRTBUS> {
                    override fun onResponse(call: Call<GetRTBUS>, response: Response<GetRTBUS>) {
                        val body = response.body()
                        val service = body?.Services
                        for (i in service?.indices ?: return) {
                            if (i == service?.size - 1) {
                                Log.i("Bus: ", Repository.busMarkerList[x].busServiceNo)
                                Repository.busMarkerList[x].busServiceNo += service[i].ServiceNo
                                return
                            }
                            Repository.busMarkerList[x].busServiceNo += service[i].ServiceNo + ", "
                        }
                    }

                    override fun onFailure(call: Call<GetRTBUS>, t: Throwable) {
                        Log.i("Live on failure", t.message?:"Null")
                    }

                })
        }
    }
}
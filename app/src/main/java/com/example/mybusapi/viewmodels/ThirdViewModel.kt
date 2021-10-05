package com.example.mybusapi.viewmodels

import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.adapters.BusAdapter
import com.example.mybusapi.retrofit.RetrofitInstance
import com.example.mybusapi.retrofit.method.bus.GetRTBUS
import com.example.mybusapi.viewdatatemplate.BusArrival
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ThirdViewModel : ViewModel() {

    var busRV : RecyclerView? = null
    lateinit var busArrivalArrayList : ArrayList<BusArrival>
    var toolbar: Toolbar? = null

    init {
        viewModelScope.launch {
            delay(2000)
            busRV = Repository.thirdFragmentShared?.requireActivity()?.findViewById(R.id.rv_busInfo)
            toolbar = Repository.thirdFragmentShared?.requireActivity()?.findViewById(R.id.toolbar)
            val button : ImageButton? = Repository.thirdFragmentShared?.requireActivity()?.findViewById(R.id.toolbarFavorite)
            button?.isVisible = true
            toolbar?.title = "Bus Stop Number: " + Repository.busStopNumber.toString()
            //
            if(Repository.busStopFavoriteList.contains(toolbar?.title.toString().substringAfter(": ").toInt())){
                button?.setImageResource(R.drawable.ic_favourite)
            }
            else{
                button?.setImageResource(R.drawable.ic_notfavourite)
            }
            //
            viewModelScope.launch(Dispatchers.Main){
                RetrofitInstance.api.getBus(Repository.busStopNumber).enqueue(object :
                    Callback<GetRTBUS> {
                    override fun onResponse(call: Call<GetRTBUS>, response: Response<GetRTBUS>) {
                        val serviceSize = response.body()?.Services?.size
                        busArrivalArrayList = ArrayList(serviceSize?:return)
                        for(i in 0 until serviceSize){
                            val curBusNumber = response.body()?.Services?.get(i)?.ServiceNo
                            var curBusArr = ""
                            var curBusArr2 = ""
                            if(response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival?.isEmpty()?:return){
                                curBusArr = "NIL"
                                curBusArr2 = "NIL"
                                busArrivalArrayList.add(BusArrival(
                                    curBusNumber?:return,
                                    curBusArr?:return,
                                    curBusArr2?:return
                                ))
                            }
                            if(response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival?.isNotEmpty()?:return && response.body()?.Services?.get(i)?.NextBus2?.EstimatedArrival?.isEmpty()?:return){
                                curBusArr = response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival?.substring(11, 16)?:return
                                curBusArr2 = "NIL"
                                if(TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival?.substring(11, 19)?:return) != ""){
                                    curBusArr = "ARR"
                                }
                                busArrivalArrayList.add(BusArrival(
                                    curBusNumber?:return,
                                    curBusArr?:return,
                                    curBusArr2?:return
                                ))
                            }
                            else{
                                curBusArr = response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival?.substring(11, 16)?:return
                                curBusArr2 = response.body()?.Services?.get(i)?.NextBus2?.EstimatedArrival?.substring(11, 16)?:return
                                if(TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival?.substring(11, 19)?:return) != ""){
                                    curBusArr = "ARR"
                                }
                                busArrivalArrayList.add(BusArrival(
                                    curBusNumber?:return,
                                    curBusArr?:return,
                                    curBusArr2?:return
                                ))
                            }
                            //var busArr = TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), curBusArr)
                            //var nextBusArr = TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), curBusArr2)
                        }
                    }

                    override fun onFailure(call: Call<GetRTBUS>, t: Throwable) {
                        Log.i("Get Bus Arrival onFailure: ", t?.message?:"NULL")
                    }
                })
                delay(2000)
                val busAdapter = BusAdapter(Repository.thirdFragmentShared?.requireContext()?:return@launch, busArrivalArrayList)
                val llm = LinearLayoutManager(Repository.thirdFragmentShared?.requireActivity(), LinearLayoutManager.VERTICAL, false)
                busRV?.layoutManager = llm
                busRV?.adapter = busAdapter

            }
        }

    }



    fun TimeDifference(curTime : String, arrTime : String): String{
        val timeFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        val curTime = LocalTime.parse(curTime, timeFormatter)
        val arrTime = LocalTime.parse(arrTime, timeFormatter)

        if(arrTime.isBefore(curTime)){
            return "ARR"
        }
        var difference = Duration.between(curTime, arrTime)

        var hours : Long = difference.toHours()
        difference = difference.minusHours(hours)
        var minutes = difference.toMinutes()
        difference = difference.minusMinutes(minutes)
        var seconds = difference.seconds

        if(minutes <= 1)return "ARR"
        return ""
    }
}
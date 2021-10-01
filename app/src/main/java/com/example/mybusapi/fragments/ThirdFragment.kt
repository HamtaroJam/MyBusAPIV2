package com.example.mybusapi.fragments

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.adapters.BusAdapter
import com.example.mybusapi.databinding.FragmentThirdBinding
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
import kotlin.math.min

class ThirdFragment : Fragment() {

    lateinit var binding: FragmentThirdBinding
    lateinit var AppCom : AppCompatActivity
    lateinit var busRV : RecyclerView
    lateinit var busArrivalArrayList : ArrayList<BusArrival>
    lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        busRV = requireActivity().findViewById(R.id.rv_busInfo)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        val button : ImageButton = requireActivity().findViewById(R.id.toolbarFavorite)
        button.isVisible = true
        toolbar.title = "Bus Stop Number: " + Repository.busStopNumber.toString()
        //
        if(Repository.busStopFavoriteList.contains(toolbar.title.toString().substringAfter(": ").toInt())){
            button.setImageResource(R.drawable.ic_favourite)
        }
        else{
            button.setImageResource(R.drawable.ic_notfavourite)
        }
        //
        lifecycleScope.launch(Dispatchers.Main){
            RetrofitInstance.api.getBus(Repository.busStopNumber).enqueue(object : Callback<GetRTBUS>{
                override fun onResponse(call: Call<GetRTBUS>, response: Response<GetRTBUS>) {
                    val serviceSize = response.body()?.Services?.size
                    busArrivalArrayList = ArrayList(serviceSize!!)
                    for(i in 0 until serviceSize!!){
                        val curBusNumber = response.body()?.Services?.get(i)?.ServiceNo
                        var curBusArr = ""
                        var curBusArr2 = ""
                        if(response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival!!.isEmpty()){
                            curBusArr = "NIL"
                            curBusArr2 = "NIL"
                            busArrivalArrayList.add(BusArrival(
                                curBusNumber!!,
                                curBusArr!!,
                                curBusArr2!!
                            ))
                        }
                        if(response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival!!.isNotEmpty() && response.body()?.Services?.get(i)?.NextBus2?.EstimatedArrival!!.isEmpty()){
                            curBusArr = response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival!!.substring(11, 16)
                            curBusArr2 = "NIL"
                            if(TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival!!.substring(11, 19)) != ""){
                                curBusArr = "ARR"
                            }
                            busArrivalArrayList.add(BusArrival(
                                curBusNumber!!,
                                curBusArr!!,
                                curBusArr2!!
                            ))
                        }
                        else{
                            curBusArr = response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival!!.substring(11, 16)
                            curBusArr2 = response.body()?.Services?.get(i)?.NextBus2?.EstimatedArrival!!.substring(11, 16)
                            if(TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), response.body()?.Services?.get(i)?.NextBus?.EstimatedArrival!!.substring(11, 19)) != ""){
                                curBusArr = "ARR"
                            }
                            busArrivalArrayList.add(BusArrival(
                                curBusNumber!!,
                                curBusArr!!,
                                curBusArr2!!
                            ))
                        }
                        //var busArr = TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), curBusArr!!)
                        //var nextBusArr = TimeDifference(Calendar.getInstance().time.toString().substring(11, 19), curBusArr2!!)
                    }
                }

                override fun onFailure(call: Call<GetRTBUS>, t: Throwable) {
                    println(t.message)
                }
            })
            delay(2000)
            val busAdapter = BusAdapter(requireContext(), busArrivalArrayList)
            val llm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            busRV.layoutManager = llm
            busRV.adapter = busAdapter

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        AppCom = requireActivity() as AppCompatActivity
        val drawer : DrawerLayout = AppCom.findViewById(R.id.drawer_layout)
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        toolbar.title = "MyBusApi"
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
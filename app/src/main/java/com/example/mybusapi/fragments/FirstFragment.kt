package com.example.mybusapi.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.databinding.FragmentFirstBinding
import com.example.mybusapi.retrofit.RetrofitInstance
import com.example.mybusapi.retrofit.method.bus.BusMarker
import com.example.mybusapi.retrofit.method.bus.GetRTBUS
import com.example.mybusapi.retrofit.method.busstop.GetALL
import com.example.mybusapi.viewmodels.FirstViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    var busMarkerList : List<BusMarker> = emptyList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.splashvm = FirstViewModel(Repository)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO){
            for(i in 0..10){
                RetrofitInstance.api.getPost(i * 500).enqueue(object : Callback<GetALL> {
                    override fun onResponse(call: Call<GetALL>, response: Response<GetALL>){
                        Log.i("Check", response.toString())

                        if(!response.isSuccessful){
                            Log.i("Failed", "Backing out")
                            //Toast.makeText(activity, "Unsuccessful Network Call!", Toast.LENGTH_LONG).show()
                            return
                        }

                        val body = response.body()!!
                        val sizeOfStop = body.value.size

                        for(x in 0 until sizeOfStop){
                            var busStopCode = body.value.get(x).BusStopCode
                            var busStopDescription = body.value.get(x).Description
                            var busStopRoad = body.value.get(x).RoadName
                            busMarkerList += BusMarker(
                                body.value.get(x).BusStopCode,
                                LatLng(body.value.get(x).Latitude, body.value.get(x).Longitude),
                                body.value.get(x).Description,
                                body.value.get(x).RoadName,
                                "Bus Stop Number: $busStopCode ::Bus Stop Station Name: $busStopDescription :::Bus Stop Road: $busStopRoad"
                            )
                        }
                        println(busMarkerList.size)
                        Repository.busMarkerList = busMarkerList

                    }

                    override fun onFailure(call: Call<GetALL>, t: Throwable) {
                        Log.i(call.toString(), t.message!!)
                    }
                })
            }
            delay(1000)
            for(x in Repository.busMarkerList.indices){
                RetrofitInstance.api.getBus(Repository.busMarkerList.get(x).busStopCode.toInt()).enqueue(object : Callback<GetRTBUS>{
                    override fun onResponse(call: Call<GetRTBUS>, response: Response<GetRTBUS>) {
                        val body = response.body()!!
                        val service = body.Services
                        for(i in service.indices){
                            if(i == service.size-1){
                                println(Repository.busMarkerList.get(x).busServiceNo)
                                Repository.busMarkerList.get(x).busServiceNo += service.get(i).ServiceNo
                                return
                            }
                            Repository.busMarkerList.get(x).busServiceNo += service.get(i).ServiceNo + ", "
                        }
                    }
                    override fun onFailure(call: Call<GetRTBUS>, t: Throwable) {
                        println(t.message)
                    }
                })
            }
        }
        lifecycleScope.launch{
            //ToDO: Use source control
            //ToDO: Refactor this to be called on 'action' instead of time
            //ToDO: Don't use !! implicit, use let or ? instead
            //ToDO: Android team: SVG TO PNG, then convert to WEBP format
            //ToDO: No println, use log.d instead
            //ToDO: Use android suggestion instead of leaving it hanging
            //From Bryan
            //ToDO: Hilt injection to inject dependency into viewmodel
            //ToDO: Launch with I/O and setvalue
            //ToDO: RecyclerView is to Recycle View
            delay(30000)
            findNavController().navigate(R.id.action_firstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
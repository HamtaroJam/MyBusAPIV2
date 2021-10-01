package com.example.mybusapi.retrofit

import com.example.mybusapi.retrofit.method.bus.GetRTBUS
import com.example.mybusapi.retrofit.method.busstop.GetALL
import com.example.mybusapi.retrofit.utils.Constant
import retrofit2.Call
import retrofit2.http.*

interface BusStopApi {
    @Headers("AccountKey:OSt0kebUR7ytazExNTBWug==")
    @GET("BusStops")
    fun getPost(@Query(Constant.Add_URL) skipCount : Int) : Call<GetALL>

    @Headers("AccountKey:OSt0kebUR7ytazExNTBWug==")
    @GET("BusArrivalv2")
    fun getBus(@Query("BusStopCode") busStopNumber : Int) : Call<GetRTBUS>
}
package com.example.mybusapi.retrofit.method.bus

data class GetRTBUS(
    val BusStopCode: String,
    val Services: List<Service>
)
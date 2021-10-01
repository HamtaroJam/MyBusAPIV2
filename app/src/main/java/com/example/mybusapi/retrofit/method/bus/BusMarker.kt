package com.example.mybusapi.retrofit.method.bus

import com.google.android.gms.maps.model.LatLng

data class BusMarker(
    var busStopCode : String,
    var busStopLatLng : LatLng,
    var busStopDescription : String,
    var busStopRoad : String,
    var busSnippets : String,
    var busServiceNo : String = ""
)

package com.example.mybusapi.viewdatatemplate

import com.google.android.gms.maps.model.LatLng

data class BusFavorite(
    var stationName : String,
    var stationRoad : String,
    var stationCode : String,
    var stationLatLng : LatLng
)

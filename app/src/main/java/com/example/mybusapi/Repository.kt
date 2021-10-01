package com.example.mybusapi

import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.mybusapi.retrofit.method.bus.BusMarker
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import java.lang.ref.WeakReference

object Repository {
    //Global Variable
    var busMarkerList : List<BusMarker> = emptyList()
    var busStopMarker : List<Marker> = emptyList()
    var busStopFavoriteList : ArrayList<Int> = ArrayList()
    var busStopNumber : Int = 0
    var geofencelist = mutableListOf<Geofence>()
    var userCurrentMarker : Marker? = null
    var mMap : GoogleMap? = null
    var gCircle : Circle? = null

}
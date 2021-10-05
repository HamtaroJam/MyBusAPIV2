package com.example.mybusapi.viewmodels

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.adapters.FavouriteAdapter
import com.example.mybusapi.viewdatatemplate.BusFavorite
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SecondViewModel: ViewModel() {
    lateinit var rvFavourite : RecyclerView
    lateinit var favArrayList : ArrayList<BusFavorite>
    lateinit var mView : MapView

init {
    viewModelScope.launch {
        delay(2000)
        //Link to map in layout
        mView = Repository.secondFragmentShared?.requireActivity()?.findViewById(R.id.mapView) as MapView
        mView.onCreate(Repository.secondSavedBundle)
        mView.onResume()
        mView.getMapAsync(InfoWindowActivity())

        rvFavourite = Repository.secondFragmentShared?.requireActivity()?.findViewById(R.id.rv_favList)?:return@launch

        GenerateFavList()
    }
}

    internal inner class InfoWindowActivity : GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {
        override fun onMapReady(googleMap: GoogleMap) {
            Repository.mMap = googleMap
            Repository.mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(Repository.secondFragmentShared?.requireContext(), R.raw.style_json))
            var shouldVisible = false

            val singapore = LatLng(1.3521, 103.8198)
            Repository.mMap?.setMinZoomPreference(11f)
            Repository.mMap?.addMarker(
                MarkerOptions()
                    .position(singapore)
                    .title("Singapore")
                    .snippet("Country: Singapore ::Population: 5.704M :::Country Size: 728.6 km² ::::Total Bus Stop: 5050")
            )
            for(i in Repository.busMarkerList.indices){
                Repository.busStopMarker += Repository.mMap?.addMarker(
                    MarkerOptions()
                        .position(Repository.busMarkerList[i].busStopLatLng)
                        .title(Repository.busMarkerList[i].busStopCode)
                        .visible(false)
                        .snippet(Repository.busMarkerList[i].busSnippets + " ::::" + Repository.busMarkerList.get(i).busServiceNo)
                )?:return
            }
            @Override
            fun onCameraChange(cameraPosition : CameraPosition){
                if(cameraPosition.zoom > 16 && !shouldVisible){
                    for(i in 0 until Repository.busStopMarker.size){
                        Repository.busStopMarker[i].isVisible = true
                    }
                    shouldVisible = true
                    return
                }
                if(cameraPosition.zoom < 16 && shouldVisible){
                    for(i in 0 until Repository.busStopMarker.size){
                        Repository.busStopMarker[i].isVisible = false
                    }
                    shouldVisible = false
                }
            }
            Repository.mMap?.setOnCameraIdleListener { onCameraChange(Repository.mMap?.cameraPosition?:return@setOnCameraIdleListener) }
            Repository.mMap?.setInfoWindowAdapter(Repository.secondFragmentShared?.requireContext()?.let { CustomInfoWindowForGoogleMap(it) })
            Repository.mMap?.setOnInfoWindowClickListener(this@InfoWindowActivity)
            Repository.mMap?.moveCamera(CameraUpdateFactory.newLatLng(singapore))

            val context = Repository.secondFragmentShared?.requireContext() as Activity
            val editText = context.findViewById<EditText>(R.id.editTextNumber)
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    return
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    return
                }
                override fun afterTextChanged(p0: Editable?) {
                    if(p0?.length == 5){
                        for(i in Repository.busStopMarker.indices){
                            if(Repository.busStopMarker[i].title == p0?.toString()){
                                viewModelScope.launch {
                                    Repository.mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(Repository.busStopMarker.get(i).position, 16.1f))
                                    delay(1000)
                                    Repository.busStopMarker[i].showInfoWindow()
                                }
                            }
                        }
                        return
                    }
                }
            })
        }
        override fun onInfoWindowClick(marker: Marker) {
            Repository.busStopNumber = marker.title.toInt()
            findNavController(Repository.secondFragmentShared?:return).navigate(R.id.action_SecondFragment_to_thirdFragment)
        }
    }

    internal inner class CustomInfoWindowForGoogleMap(context: Context) :GoogleMap.InfoWindowAdapter{
        var mContext = context
        private var mWindow = (mContext as Activity).layoutInflater.inflate(R.layout.custom_infowindow, null)

        override fun getInfoWindow(marker: Marker): View? {
            rendWindowText(marker, mWindow)
            return mWindow
        }

        override fun getInfoContents(marker: Marker): View? {
            rendWindowText(marker, mWindow)
            return mWindow
        }

        private fun rendWindowText(marker: Marker, view: View){
            val tvStationName = view.findViewById<TextView>(R.id.stationName)
            val tvStationNumber = view.findViewById<TextView>(R.id.stationNumber)
            val tvStationRoad = view.findViewById<TextView>(R.id.stationRoad)
            val tvStationBus = view.findViewById<TextView>(R.id.stationBus)

            tvStationName.text = " " + marker.snippet.substringBefore("::")
            tvStationNumber.text = " " + marker.snippet.substringAfter("::").substringBefore(":::")
            tvStationRoad.text = " " + marker.snippet.substringAfter(":::").substringBefore("::::")
            if(marker.snippet.substringAfter("::::") == "")return
            tvStationBus.text = " " + marker.snippet.substringAfter("::::")
        }
    }

    fun GenerateFavList(){
        if(Repository.busStopFavoriteList.isNotEmpty()){
            favArrayList = ArrayList()
            for(i in 0 until Repository.busStopFavoriteList.size){
                for(x in Repository.busMarkerList.indices){
                    if(Repository.busMarkerList[x].busStopCode.contains(Repository.busStopFavoriteList[i].toString())){
                        favArrayList.add(
                            BusFavorite(
                                Repository.busMarkerList[x].busStopDescription,
                                Repository.busMarkerList[x].busStopRoad,
                                Repository.busMarkerList[x].busStopCode,
                                Repository.busMarkerList[x].busStopLatLng
                            )
                        )
                    }
                }
            }
            val favAdapter = FavouriteAdapter(Repository.secondFragmentShared?.requireContext(), favArrayList)
            val llm = LinearLayoutManager(Repository.secondFragmentShared?.requireContext(), LinearLayoutManager.VERTICAL, false)
            rvFavourite.layoutManager = llm
            rvFavourite.adapter = favAdapter
        }
    }
}
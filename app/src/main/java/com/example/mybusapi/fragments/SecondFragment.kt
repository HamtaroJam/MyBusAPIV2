package com.example.mybusapi.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.mybusapi.R
import com.example.mybusapi.databinding.FragmentSecondBinding
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.Repository
import com.example.mybusapi.Repository.busMarkerList
import com.example.mybusapi.Repository.busStopFavoriteList
import com.example.mybusapi.Repository.mMap
import com.example.mybusapi.adapters.BusAdapter
import com.example.mybusapi.adapters.FavouriteAdapter
import com.example.mybusapi.databinding.FragmentFirstBinding
import com.example.mybusapi.retrofit.RetrofitInstance
import com.example.mybusapi.viewdatatemplate.BusFavorite
import com.example.mybusapi.viewmodels.SecondViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*
import java.lang.ref.WeakReference


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    lateinit var _binding: FragmentSecondBinding
    lateinit var _binding02 : FragmentFirstBinding
    lateinit var secondViewModel : SecondViewModel
    lateinit var AppCom : AppCompatActivity
    lateinit var rvFavourite : RecyclerView
    lateinit var favArrayList : ArrayList<BusFavorite>
    //lateinit var mMap : GoogleMap
    lateinit var mView : MapView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        secondViewModel = ViewModelProvider(this).get(SecondViewModel::class.java)
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        _binding02 = FragmentFirstBinding.inflate(inflater, container, false)
        binding.fragment02vm = secondViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCom = requireActivity() as AppCompatActivity
        AppCom.supportActionBar?.show()
        val button : ImageButton = requireActivity().findViewById(R.id.toolbarFavorite)
        button.isVisible = false
        //Link to map in layout
        mView = activity?.findViewById(R.id.mapView) as MapView
        mView.onCreate(savedInstanceState)
        mView.onResume()
        mView.getMapAsync(InfoWindowActivity())

        rvFavourite = requireActivity().findViewById(R.id.rv_favList)

        if(busStopFavoriteList.isNotEmpty()){
            favArrayList = ArrayList()
            for(i in 0 until busStopFavoriteList.size){
                for(x in 0 until busMarkerList.size){
                    if(busMarkerList.get(x).busStopCode.contains(busStopFavoriteList.get(i).toString())){
                        favArrayList.add(
                            BusFavorite(
                                busMarkerList.get(x).busStopDescription,
                                busMarkerList.get(x).busStopRoad,
                                busMarkerList.get(x).busStopCode,
                                busMarkerList.get(x).busStopLatLng
                        )
                        )
                    }
                }
            }
            val favAdapter = FavouriteAdapter(requireContext(), favArrayList)
            val llm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvFavourite.layoutManager = llm
            rvFavourite.adapter = favAdapter
        }

//        var linearL : LinearLayout = requireActivity().findViewById(R.id.fav_linear01)
//        var cardView = CardView(this.requireContext())
//        var favText = TextView(this.context)
//        lifecycleScope.launch(Dispatchers.Main){
//            for(i in Repository.busStopFavoriteList.size-1 downTo 0){
//                favText.id = linearL.childCount
//                favText.setText("${Repository.busStopFavoriteList.get(i)}")
//                cardView.id = linearL.childCount
//                cardView.addView(favText)
//                linearL.addView(cardView)
//            }
//        }

//        lifecycleScope.launch {
//            delay(3000)
//            findNavController().navigate(R.id.action_SecondFragment_to_thirdFragment)
//        }
    }

    internal inner class InfoWindowActivity : GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap
            mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json))
            var shouldbeVisible = false

            val singapore = LatLng(1.3521, 103.8198)
            mMap?.setMinZoomPreference(11f)
            mMap?.addMarker(
                MarkerOptions()
                    .position(singapore)
                    .title("Singapore")
                    .snippet("Country: Singapore ::Population: 5.704M :::Country Size: 728.6 km² ::::Total Bus Stop: 5050")
            )
            for(i in 0 until Repository.busMarkerList.size){
                Repository.busStopMarker += mMap!!.addMarker(
                    MarkerOptions()
                        .position(Repository.busMarkerList.get(i).busStopLatLng)
                        .title(Repository.busMarkerList.get(i).busStopCode)
                        .visible(false)
                        .snippet(Repository.busMarkerList.get(i).busSnippets + " ::::" + Repository.busMarkerList.get(i).busServiceNo)
                )
            }
            @Override
            fun onCameraChange(cameraPosition : CameraPosition){
                println(googleMap.cameraPosition.zoom)
                if(cameraPosition.zoom > 16 && !shouldbeVisible){
                    for(i in 0 until Repository.busStopMarker.size){
                        Repository.busStopMarker.get(i).isVisible = true
                    }
                    shouldbeVisible = true
                    return
                }
                if(cameraPosition.zoom < 16 && shouldbeVisible){
                    for(i in 0 until Repository.busStopMarker.size){
                        Repository.busStopMarker.get(i).isVisible = false
                    }
                    shouldbeVisible = false
                }
            }
            mMap?.setOnCameraIdleListener { onCameraChange(mMap!!.cameraPosition) }
            mMap?.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(requireContext()))
            mMap?.setOnInfoWindowClickListener(this@InfoWindowActivity)
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(singapore))

            val context = requireContext() as Activity
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
                        for(i in 0 until Repository.busStopMarker.size){
                            if(Repository.busStopMarker.get(i).title == p0?.toString()){
                                lifecycleScope.launch {
                                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(Repository.busStopMarker.get(i).position, 16.1f))
                                    delay(1000)
                                    Repository.busStopMarker.get(i).showInfoWindow()
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
            findNavController().navigate(R.id.action_SecondFragment_to_thirdFragment)
        }

    }

    internal inner class CustomInfoWindowForGoogleMap(context: Context) :GoogleMap.InfoWindowAdapter{
        var mContext = context
        var mWindow = (mContext as Activity).layoutInflater.inflate(R.layout.custom_infowindow, null)

        override fun getInfoWindow(marker: Marker): View? {
            rendowWindowText(marker, mWindow)
            return mWindow
        }

        override fun getInfoContents(marker: Marker): View? {
            rendowWindowText(marker, mWindow)
            return mWindow
        }

        fun rendowWindowText(marker: Marker, view: View){
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

    override fun onDestroyView() {
        super.onDestroyView()
        val drawer : DrawerLayout = AppCom.findViewById(R.id.drawer_layout)

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
    }
}
package com.example.mybusapi.adapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.viewdatatemplate.BusArrival
import com.example.mybusapi.viewdatatemplate.BusFavorite
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope

class FavouriteAdapter : RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    var context : Context
    var favouriteArrayList : ArrayList<BusFavorite>

    constructor(context : Context, favouriteArrayList : ArrayList<BusFavorite>){
        this.context = context
        this.favouriteArrayList = favouriteArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.custom_cardview_favourite, parent, false)
        return FavouriteAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
        var model : BusFavorite = favouriteArrayList.get(position)
        holder.stationName.setText(model.stationName)
        holder.roadName.setText(model.stationRoad)
        holder.stationNumber.setText(model.stationCode)
        holder.stationLatLng = model.stationLatLng
        holder.zoomToLocation.background = ColorDrawable(0x000000FF)
        holder.zoomToLocation.setOnClickListener { run{
            Repository.mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(holder.stationLatLng!!, 16.1f))
        } }
        holder.busInfo.background = ColorDrawable(0x000000FF)
        holder.busInfo.setOnClickListener { run{
            println(holder.stationNumber.text.toString().toInt())
            Repository.busStopNumber = holder.stationNumber.text.toString().toInt()
            (context as Activity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_SecondFragment_to_thirdFragment)
        } }
    }

    override fun getItemCount(): Int {
        return favouriteArrayList.size
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var stationName : TextView
        var roadName : TextView
        var stationNumber : TextView
        var stationLatLng : LatLng? = null
        var zoomToLocation : ImageButton
        var busInfo : ImageButton
        constructor(itemView : View) : super(itemView){
            stationName = itemView.findViewById(R.id.tv_favStationName)
            roadName = itemView.findViewById(R.id.tv_favRoadName)
            stationNumber = itemView.findViewById(R.id.tv_favStationNumber)
            zoomToLocation = itemView.findViewById(R.id.ib_Location)
            busInfo = itemView.findViewById(R.id.ib_BusInfo)
        }
    }

    suspend fun coroutine(){

    }
}
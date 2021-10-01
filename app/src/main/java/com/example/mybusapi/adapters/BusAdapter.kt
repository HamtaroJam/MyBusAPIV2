package com.example.mybusapi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.R
import com.example.mybusapi.viewdatatemplate.BusArrival
import io.reactivex.rxjava3.annotations.NonNull

class BusAdapter : RecyclerView.Adapter<BusAdapter.ViewHolder> {

    var context : Context
    var busArrivalArrayList : ArrayList<BusArrival>

    constructor(context : Context, busArrivalArrayList : ArrayList<BusArrival>){
        this.context = context
        this.busArrivalArrayList = busArrivalArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusAdapter.ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.custom_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusAdapter.ViewHolder, position: Int) {
        var model : BusArrival = busArrivalArrayList.get(position)
        holder.busNumber.setText(model.busNumber)
        holder.busArrival.setText("" + model.busArrTime)
        holder.busArrival2.setText("" + model.bus2ArrTime)
    }

    override fun getItemCount(): Int {
        return busArrivalArrayList.size
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var busNumber : TextView
        var busArrival : TextView
        var busArrival2 : TextView
        constructor(itemView : View) : super(itemView){
            busNumber = itemView.findViewById(R.id.tv_busNumber)
            busArrival = itemView.findViewById(R.id.tv_busUpcoming)
            busArrival2 = itemView.findViewById(R.id.tv_busUpcoming2)
        }
    }
}
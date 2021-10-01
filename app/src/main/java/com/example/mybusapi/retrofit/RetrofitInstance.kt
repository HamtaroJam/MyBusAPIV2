package com.example.mybusapi.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://datamall2.mytransport.sg/ltaodataservice/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val api: BusStopApi by lazy {
        retrofit.create(BusStopApi::class.java)
    }
}
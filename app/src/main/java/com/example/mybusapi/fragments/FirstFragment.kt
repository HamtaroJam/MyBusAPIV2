package com.example.mybusapi.fragments

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.databinding.FragmentFirstBinding
import com.example.mybusapi.retrofit.RetrofitInstance
import com.example.mybusapi.retrofit.method.bus.BusMarker
import com.example.mybusapi.retrofit.method.bus.GetRTBUS
import com.example.mybusapi.retrofit.method.busstop.GetALL
import com.example.mybusapi.viewmodels.FirstViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.splashvm = FirstViewModel(Repository)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Repository.firstFragmentShared = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
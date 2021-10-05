package com.example.mybusapi.fragments

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.R
import com.example.mybusapi.Repository
import com.example.mybusapi.adapters.BusAdapter
import com.example.mybusapi.databinding.FragmentThirdBinding
import com.example.mybusapi.retrofit.RetrofitInstance
import com.example.mybusapi.retrofit.method.bus.GetRTBUS
import com.example.mybusapi.viewdatatemplate.BusArrival
import com.example.mybusapi.viewmodels.ThirdViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class ThirdFragment : Fragment() {

    lateinit var binding: FragmentThirdBinding
    lateinit var thirdViewModel : ThirdViewModel
    lateinit var AppCom : AppCompatActivity
    lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thirdViewModel = ViewModelProvider(this).get(ThirdViewModel::class.java)
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        binding.frag3vm = thirdViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Repository.thirdFragmentShared = this

    }

    override fun onDestroyView() {
        super.onDestroyView()
        AppCom = requireActivity() as AppCompatActivity
        toolbar = requireActivity().findViewById(R.id.toolbar)
        val drawer : DrawerLayout = AppCom.findViewById(R.id.drawer_layout)
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        toolbar.title = "MyBusApi"
    }
}
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
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybusapi.Repository
import com.example.mybusapi.Repository.busMarkerList
import com.example.mybusapi.Repository.busStopFavoriteList
import com.example.mybusapi.Repository.mMap
import com.example.mybusapi.adapters.FavouriteAdapter
import com.example.mybusapi.databinding.FragmentFirstBinding
import com.example.mybusapi.viewdatatemplate.BusFavorite
import com.example.mybusapi.viewmodels.SecondViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    lateinit var AppCom : AppCompatActivity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        binding.fragment02vm = SecondViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Repository.secondSavedBundle = savedInstanceState
        Repository.secondFragmentShared = this
        AppCom = requireActivity() as AppCompatActivity
        AppCom.supportActionBar?.show()
        val button : ImageButton? = Repository.secondFragmentShared?.requireActivity()?.findViewById(R.id.toolbarFavorite)
        button?.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val drawer : DrawerLayout = AppCom.findViewById(R.id.drawer_layout)
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
    }
}
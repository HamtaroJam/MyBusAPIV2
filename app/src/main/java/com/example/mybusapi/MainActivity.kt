package com.example.mybusapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import android.location.LocationListener
import android.location.Location
import android.location.LocationManager
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LocationListener {

    lateinit var geoFencingClient : GeofencingClient
    lateinit var lm : LocationManager
    lateinit var location : Location
    var longitude : Double = 0.0
    var latitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNavViewListener()
        //Find drawer
        val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        //Find toolbar
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        val buttonFavourite : ImageButton = ImageButton(this)
        buttonFavourite.setImageResource(R.drawable.ic_notfavourite)
        val l3 : Toolbar.LayoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
        l3.gravity = Gravity.END
        buttonFavourite.id = R.id.toolbarFavorite
        buttonFavourite.layoutParams = l3
        buttonFavourite.background = ColorDrawable(0x000000FF)
        buttonFavourite.setPadding(0,0,32,0)
        buttonFavourite.setOnClickListener { run{
            if(Repository.busStopFavoriteList.contains(toolbar.title.toString().substringAfter(": ").toInt())){
                Repository.busStopFavoriteList.remove(toolbar.title.toString().substringAfter(": ").toInt())
                buttonFavourite.setImageResource(R.drawable.ic_notfavourite)
            }
            else{
                Repository.busStopFavoriteList.add(toolbar.title.toString().substringAfter(": ").toInt())
                buttonFavourite.setImageResource(R.drawable.ic_favourite)
            }
        } }
        toolbar.addView(buttonFavourite)
        //Set toolbar to be action bar
        setSupportActionBar(toolbar)
        //Automatically add hamburger menu to toolbar and make it toggle navigation drawer onClick
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        lm = getSystemService(androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE) as android.location.LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
            return;
        }

        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        longitude = location.longitude
        latitude = location.latitude
//        locationListener = LocationListener {
//            fun onLocationChanged(location : Location){
//                longitude = location.longitude
//                latitude = location.latitude
//            }
//        }
        println("latitude: " + latitude + " longitude: " + longitude)
        geoFencingClient = LocationServices.getGeofencingClient(this)
        Repository.geofencelist.add(Geofence.Builder()
            .setRequestId("Hello")
            .setCircularRegion(latitude, longitude, 10f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
        )
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.incoming_bus -> {
                println("WORKS?")
            }
            else -> {
                println("NOT WORKING?")
            }
        }
        val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return false
    }

    fun setNavViewListener(){
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()
    }

    override fun onLocationChanged(location: Location) {
        if(Repository.userCurrentMarker != null){
            Repository.userCurrentMarker!!.remove()
            Repository.gCircle!!.remove()
        }
        longitude = location.longitude
        latitude = location.latitude
        var center = LatLng(latitude, longitude)
        if(Repository.mMap != null){
            Repository.userCurrentMarker = Repository.mMap?.addMarker(
                MarkerOptions()
                    .position(center)
                    .title("User")
                    .visible(true)
                    .snippet( "A  ::  B" + "C  :::  D" + "F ::::  G")
                    .zIndex(10f)
            )
            Repository.gCircle = Repository.mMap?.addCircle(
                CircleOptions()
                    .center(center)
                    .radius(3000.0)
                    .visible(true)
                    .strokeColor(Color.RED)
                    .fillColor(0x220000FF)
                    .zIndex(100f)
            )
        }
        println("Total bus stop: " + Repository.busStopMarker.size)
        if(Repository.busStopMarker.size < 5000) return
        for(i in 0 until Repository.busStopMarker.size){
            var float = distanceBetween(center, Repository.busStopMarker.get(i).position)
            if(float < 3000f){
                println("True: $float")
                Repository.busStopMarker.get(i).isVisible = true
            }
            if(float > 3000f){
                println("False: $float")
                Repository.busStopMarker.get(i).isVisible = false
            }
        }
    }
    private fun distanceBetween(center : LatLng, mLatLng : LatLng) : Float{
        var floatArray = FloatArray(1)
        Location.distanceBetween(center.latitude, center.longitude, mLatLng.latitude, mLatLng.longitude, floatArray)
        return floatArray[0]
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}
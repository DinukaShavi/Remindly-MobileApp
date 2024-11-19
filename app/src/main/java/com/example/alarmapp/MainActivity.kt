package com.example.alarmapp


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.alarmapp.databinding.ActivityMainBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        // Initialize Map Fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Request Location Permissions
        requestLocationPermission()

        // Observe ViewModel for destination alarm
        viewModel.isNearDestination.observe(this) { isNear ->
            if (isNear) {
                triggerAlarm()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set default location (e.g., a popular destination)
        val defaultLocation = LatLng(37.7749, -122.4194) // Example: San Francisco
        googleMap.addMarker(MarkerOptions().position(defaultLocation).title("Destination"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Set the destination in ViewModel
        viewModel.setDestination(defaultLocation)

        // Enable location if permission is granted
        enableUserLocation()
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            googleMap.setOnMyLocationChangeListener { location ->
                viewModel.updateCurrentLocation(location)
            }
        }
    }

    private fun requestLocationPermission() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                enableUserLocation()
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun triggerAlarm() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(1000) // Vibrate for 1 second
        binding.alarmTextView.text = "You have arrived at your destination!"
    }
}

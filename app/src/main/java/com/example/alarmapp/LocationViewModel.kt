package com.example.alarmapp

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {

    private val _isNearDestination = MutableLiveData(false)
    val isNearDestination: LiveData<Boolean> get() = _isNearDestination

    private var destination: LatLng? = null

    fun setDestination(destination: LatLng) {
        this.destination = destination
    }

    fun updateCurrentLocation(currentLocation: Location) {
        destination?.let { dest ->
            val destinationLocation = Location("").apply {
                latitude = dest.latitude
                longitude = dest.longitude
            }
            val distance = currentLocation.distanceTo(destinationLocation)

            // Set proximity threshold, e.g., 100 meters
            _isNearDestination.value = distance < 100
        }
    }
}

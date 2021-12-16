package com.salva.grainchainchallenge.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class LocationService : Service() {
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(
                    "mylog",
                    "Lat is: " + locationResult.getLastLocation().getLatitude()
                        .toString() + ", " + "Lng is: " + locationResult.getLastLocation()
                        .getLongitude()
                )
                geoLocation.value?.apply {
                    add(LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude()))
                    geoLocation.postValue(this)
                }

            }
        }
    }

   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       geoLocation.value = ArrayList()
        requestLocation()
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationRequest = LocationRequest()
        locationRequest.setInterval(1000)
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }
    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val geoLocation =
            MutableLiveData<ArrayList<LatLng>>()
        val timeRunInSeconds =
            MutableLiveData<Long>()

    }
}
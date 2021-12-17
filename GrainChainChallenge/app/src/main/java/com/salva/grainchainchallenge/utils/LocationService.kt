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
import androidx.lifecycle.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.salva.grainchainchallenge.utils.Constans.UPDATE_INTERVAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationService : LifecycleService() {
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null

    private var isTimerEnabled = false
    private var timeStarted = 0L
    private var lapTime = 0L
    private var timeRun = 0L



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
        isStartService.observe(this, Observer {
            if(!it){
                stopService()
            }else{
                startTimer()
            }
        })
    }

   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       geoLocation.value = ArrayList()
       requestLocation()
       startTimer()
       return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationRequest = LocationRequest()
        locationRequest.setInterval(UPDATE_INTERVAL)
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }
    fun stopService(){
        timeRoute.postValue(timeRun/1000)
        timeRun = 0L
        lapTime = 0L
        stopForeground(true)
        stopSelf()

    }
    private fun startTimer() {

        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isStartService.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                //timeRoute.postValue((timeRun + lapTime)/1000)
                timeRun += lapTime
                delay(500)
            }

        }
    }
    companion object {
        val isStartService = MutableLiveData<Boolean>()
        val geoLocation =
            MutableLiveData<ArrayList<LatLng>>()
        val timeRoute =
            MutableLiveData<Long>()

    }

}
package com.salva.grainchainchallenge.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng

object Utils {

    fun checkPermissions(context: Context) : Boolean{
        var permission = false

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED    )
            permission = true


        return permission
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun distanceRoute(listPoints : ArrayList<LatLng>):Double{
        var distance = 0.0

        listPoints.forEachIndexed { index, point ->

            if(index>0){
                var resultDistance = FloatArray(1)
                var pointA = listPoints[index -1]
                var pointB = point
                Location.distanceBetween(pointA.latitude,pointA.longitude,pointB.latitude,pointB.longitude,resultDistance)
                distance += resultDistance[0]
            }
        }

        return distance
    }

    fun getFormattedTime(sec: Long): String {

        val hours = sec /3600
        val minutes = sec /60%60
        val seconds = sec %60

        return "${if(hours < 10) "0" else ""}$hours:" +
                "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds"

    }

}
package com.salva.grainchainchallenge.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.DecimalFormat


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

    fun distanceRoute(listPoints: ArrayList<LatLng>):Double{
        var distance = 0.0

        listPoints.forEachIndexed { index, point ->

            if(index>0){
                var resultDistance = FloatArray(1)
                var pointA = listPoints[index - 1]
                var pointB = point
                Location.distanceBetween(
                    pointA.latitude,
                    pointA.longitude,
                    pointB.latitude,
                    pointB.longitude,
                    resultDistance
                )
                distance += resultDistance[0]
            }
        }

        return distance
    }

    fun convertSecondsToHMmSs(seconds: Long): String {
        val s = seconds % 60
        val m = seconds / 60 % 60
        val h = seconds / (60 * 60) % 24
        return String.format("%d:%02d:%02d", h, m, s)
    }

    fun convertDistanceToFormat(quantity: Double): String{
        return DecimalFormat("#.###").format(quantity)
    }

    fun convertListPointToString(data: ArrayList<LatLng>):String{
        val gson = Gson()
        return gson.toJson(data)
    }

    fun convertStringToListPoints(data: String): ArrayList<LatLng>{
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return Gson().fromJson(data, listType)
    }

}
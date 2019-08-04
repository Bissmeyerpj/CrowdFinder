package com.example.crowdfinder

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.LocationManager
import android.support.v4.content.ContextCompat.getSystemService



class LocationGPS(val context: Context) {

//    var latitude: Any? = null
//    var longitude: Any? = null
//
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    init {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//        getLastLocation()
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun getLastLocation(){
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                latitude =  location?.latitude
//                longitude = location?.longitude
//                Log.d(Constants.TAG, "Location recieved")
//            }
//            .addOnFailureListener {
//                Log.d(Constants.TAG, "Location Failure")
//            }
//    }


}

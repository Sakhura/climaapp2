package com.sakhura.climaapp2.utils

import android.content.Context
import android.health.connect.datatypes.ExerciseRoute.Location
import android.location.LocationListener
import android.location.LocationManager

object LocationHelper {
    fun obtenerUbicacion(context: Context, callback: (Location?) -> Unit) {
        val locationManager =context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
           val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!isGpsEnabled && !isNetworkEnabled){
                callback(null)
                return
            }
            val locationProvider = object : LocationListener{
                override fun onLocationChanged(location: Location) {
                    locationManager.removeUpdates(this)
                    callback(location)}
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                callback(null)
            }
        }
        val lastKnownLocation = ig(isGPSEnabled){
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }else{
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if(lastKnownLocation != null){

            callback(lastKnownLocation)
        }else{
            callback(null)
        }
    }
}
package com.sakhura.climaapp2.utils

import android.content.Context
import android.health.connect.datatypes.ExerciseRoute.Location

object LocationHelper {
    fun obtenerUbicacion(context: Context, callback: (Location?) -> Unit) {
        val locationManager =context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                object: LocationListener {
                    override fun onLocationChanged(location: Location) {
                        callback(location)
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        callback(null)
                    }
                },
                null
            )
        } catch (e: SecurityException) {
            callback(null)
            }
    }
}
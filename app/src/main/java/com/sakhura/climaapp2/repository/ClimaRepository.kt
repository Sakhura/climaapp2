package com.sakhura.climaapp2.repository

import android.util.Log
import com.sakhura.climaapp2.api.ClimaApiService
import com.sakhura.climaapp2.api.RetrofitClient
import com.sakhura.climaapp2.cache.CacheManager
import com.sakhura.climaapp2.model.ClimaResponse
import com.sakhura.climaapp2.model.PronosticoResponse
import com.sakhura.climaapp2.utils.NetworkManager

class ClimaRepository {
    private val api: ClimaApiService = RetrofitClient.instance.create(ClimaApiService::class.java)
    private val apiKey = "cc5f5c259c2348ba4fc544ae184ad7c8"
    private val cacheManager = CacheManager()
    private val appContext = context.applicationContext

    companion object{
        private const val TAG = "ClimaRepository"
    }

    suspend fun obtenerClima(ciudad: String): ClimaResponse {
        val response = api.getClimaPorCiudad(ciudad, apiKey)

        //validacion de internet
        val isOnline = NetworkManager.isNetworkAvailable(appContext)
        val networkType = NetworkManager.getNetworkType(appContext)

        Log.d(TAG, "Estado de red: Online=$isOnline, Tipo de red: $networkType")


        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else {
            throw Exception("Error en la API: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun obtenerPronostico(ciudad: String): PronosticoResponse {
        val response = api.getPronosticoPorCiudad(ciudad, apiKey)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else {
            throw Exception("Error en la API: ${response.code()} - ${response.message()}")
        }
    }
}
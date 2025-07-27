package com.sakhura.climaapp2.repository

import android.util.Log
import com.sakhura.climaapp2.api.ClimaApiService
import com.sakhura.climaapp2.api.RetrofitClient
import com.sakhura.climaapp2.model.ClimaResponse
import com.sakhura.climaapp2.model.PronosticoResponse

class ClimaRepository {
    private val api: ClimaApiService = RetrofitClient.instance.create(ClimaApiService::class.java)
    private val apiKey = "b3edc70f86c12b85d2fb8a5180cc1285"

    companion object {
        private const val TAG = "ClimaRepository"
    }

    suspend fun obtenerClima(ciudad: String): ClimaResponse {
        Log.d(TAG, "Obteniendo clima para: $ciudad")

        try {
            val response = api.getClimaPorCiudad(ciudad, apiKey)

            if (response.isSuccessful) {
                Log.d(TAG, "Clima obtenido exitosamente para: $ciudad")
                return response.body() ?: throw Exception("Respuesta vacía del servidor")
            } else {
                Log.e(TAG, "Error en API: ${response.code()} - ${response.message()}")
                when (response.code()) {
                    404 -> throw Exception("Ciudad no encontrada")
                    401 -> throw Exception("Error de autenticación API")
                    429 -> throw Exception("Demasiadas solicitudes, intente más tarde")
                    500 -> throw Exception("Error interno del servidor")
                    else -> throw Exception("Error del servidor: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener clima", e)
            throw e
        }
    }

    suspend fun obtenerPronostico(ciudad: String): PronosticoResponse {
        Log.d(TAG, "Obteniendo pronóstico para: $ciudad")

        try {
            val response = api.getPronosticoPorCiudad(ciudad, apiKey)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Pronóstico obtenido exitosamente para: $ciudad")
                return response.body() ?: throw Exception("Respuesta vacía del servidor")
            } else {
                Log.e(TAG, "Error en API pronóstico: ${response.code()} - ${response.message()}")
                when (response.code()) {
                    404 -> throw Exception("Ciudad no encontrada")
                    401 -> throw Exception("Error de autenticación API")
                    429 -> throw Exception("Demasiadas solicitudes, intente más tarde")
                    500 -> throw Exception("Error interno del servidor")
                    else -> throw Exception("Error del servidor: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener pronóstico", e)
            throw e
        }
    }
}
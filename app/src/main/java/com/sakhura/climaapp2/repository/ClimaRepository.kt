package com.sakhura.climaapp2.repository

import com.sakhura.climaapp2.api.ClimaApiService
import com.sakhura.climaapp2.api.RetrofitClient
import com.sakhura.climaapp2.model.ClimaResponse
import com.sakhura.climaapp2.model.PronosticoResponse

class ClimaRepository {
    private val api: ClimaApiService = RetrofitClient.instance.create(ClimaApiService::class.java)
    private val apiKey = "cc5f5c259c2348ba4fc544ae184ad7c8"

    suspend fun obtenerClima(ciudad: String): ClimaResponse {
        val response = api.getClimaPorCiudad(ciudad, apiKey)
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
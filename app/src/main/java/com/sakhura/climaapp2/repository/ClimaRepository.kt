package com.sakhura.climaapp2.repository

import com.sakhura.climaapp2.api.RetrofitClient
import com.sakhura.climaapp2.model.ClimaResponse

class ClimaRepository {
    private val api: RetrofitClient.instance.create(ClimaApiService::class.java)
    private val apiKey = "cc5f5c259c2348ba4fc544ae184ad7c8"

    suspend fun obtenerClima(Ciudad: String): api.getClimaPorCiudad(Ciudad, apiKey)
    suspend fun obtenerPronostico(Ciudad: String): api.getPronosticoPorCiudad(Ciudad, apiKey)
    }

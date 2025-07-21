package com.sakhura.climaapp2.api

interface ClimaApiService {
    @GET("weather")
        suspend fun getClimaPorCiudad(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
        ): Response<ClimaResponse>

        @GET("forecast")
        suspend fun getPronosticoPorCiudad(
            @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
        ): Response<PronosticoResponse>
}
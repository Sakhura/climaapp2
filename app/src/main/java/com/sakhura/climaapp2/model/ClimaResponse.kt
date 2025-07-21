package com.sakhura.climaapp2.model

data class ClimaResponse(
        val nombre: String,
        val main: String,
        val weather: List<Weather>,
)

data class  Main(
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double

)

data class Weather(
  //  val id: Int,
   // val main: String,
    val description: String,
    val icon: String
)
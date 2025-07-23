package com.sakhura.climaapp2.model

import androidx.room.Entity

@Entity(tableName = "ciudades")
data class Ciudad (
    @PrimaryKey val nombre: String
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val ultimaConsulta: Long = System.currentTimeMillis()
)
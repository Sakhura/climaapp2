package com.sakhura.climaapp2.database.dao

import androidx.room.*
import com.sakhura.climaapp2.entities.ClimaEntity
import com.sakhura.climaapp2.entities.PronosticoEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE
import retrofit2.http.Query

@Dao
interface ClimaDao {

    //Clima Actual
    @Query("SELECT * FROM clima_actual WHERE ciudad = :ciudad")
    suspend fun obtenerClimaActual(ciudad: String): ClimaEntity?

    @Query("SELECT * FROM clima_actual ORDER BY fechaActualizacion DESC")
    fun getAllClimasFlow(): Flow<List<ClimaEntity>>

    @Query("SELECT * FROM clima_actual ORDER BY fechaActualizacion DESC)
            suspend fun getAllClimas(): List<ClimaEntity>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertarClima(clima: ClimaEntity)

        @Delete
        suspend fun eliminarClima(clima: ClimaEntity)

        //Pronostico
    @Query("SELECT * FROM pronostico WHERE ciudad = :ciudad")
    suspend fun getPronosticoByCiudad(ciudad: String): List<PronosticoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPronostico(pronostico: PronosticoEntity)

    @Query("DELETE FROM pronostico WHERE ciudad = :ciudad")
    suspend fun eliminarPronostico(ciudad: String)

    //Utilidades

    @Query("SELECT COUNT(*) FROM clima_actual")
    suspend fun contarClimas(): Int

    @Query("SELECT COUNT(*) FROM pronostico")
    suspend fun contarPronosticos(): Int

}
package com.sakhura.climaapp2.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.sakhura.climaapp2.model.ClimaResponse
import com.sakhura.climaapp2.model.PronosticoResponse

class CacheManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val CACHE_PREFS = "clima_cache"
        private const val CLIMA_PREFIX = "clima_"
        private const val PRONOSTICO_PREFIX = "pronostico_"
        private const val TIMESTAMP_SUFFIX = "_timestamp"
        private const val CACHE_DURATION = 10 * 60 * 1000L // 10 minutos
    }

    fun saveClima(ciudad: String, clima: ClimaResponse) {
        val key = CLIMA_PREFIX + ciudad.lowercase()
        val timestampKey = key + TIMESTAMP_SUFFIX
        
        prefs.edit()
            .putString(key, gson.toJson(clima))
            .putLong(timestampKey, System.currentTimeMillis())
            .apply()
    }

    fun getClima(ciudad: String): ClimaResponse? {
        val key = CLIMA_PREFIX + ciudad.lowercase()
        val timestampKey = key + TIMESTAMP_SUFFIX
        
        val timestamp = prefs.getLong(timestampKey, 0)
        val isExpired = System.currentTimeMillis() - timestamp > CACHE_DURATION
        
        if (isExpired) {
            clearClima(ciudad)
            return null
        }
        
        val climaJson = prefs.getString(key, null) ?: return null
        return try {
            gson.fromJson(climaJson, ClimaResponse::class.java)
        } catch (e: Exception) {
            clearClima(ciudad)
            null
        }
    }

    fun savePronostico(ciudad: String, pronostico: PronosticoResponse) {
        val key = PRONOSTICO_PREFIX + ciudad.lowercase()
        val timestampKey = key + TIMESTAMP_SUFFIX
        
        prefs.edit()
            .putString(key, gson.toJson(pronostico))
            .putLong(timestampKey, System.currentTimeMillis())
            .apply()
    }

    fun getPronostico(ciudad: String): PronosticoResponse? {
        val key = PRONOSTICO_PREFIX + ciudad.lowercase()
        val timestampKey = key + TIMESTAMP_SUFFIX
        
        val timestamp = prefs.getLong(timestampKey, 0)
        val isExpired = System.currentTimeMillis() - timestamp > CACHE_DURATION
        
        if (isExpired) {
            clearPronostico(ciudad)
            return null
        }
        
        val pronosticoJson = prefs.getString(key, null) ?: return null
        return try {
            gson.fromJson(pronosticoJson, PronosticoResponse::class.java)
        } catch (e: Exception) {
            clearPronostico(ciudad)
            null
        }
    }

    private fun clearClima(ciudad: String) {
        val key = CLIMA_PREFIX + ciudad.lowercase()
        val timestampKey = key + TIMESTAMP_SUFFIX
        prefs.edit()
            .remove(key)
            .remove(timestampKey)
            .apply()
    }

    private fun clearPronostico(ciudad: String) {
        val key = PRONOSTICO_PREFIX + ciudad.lowercase()
        val timestampKey = key + TIMESTAMP_SUFFIX
        prefs.edit()
            .remove(key)
            .remove(timestampKey)
            .apply()
    }

    fun clearAllCache() {
        prefs.edit().clear().apply()
    }
}
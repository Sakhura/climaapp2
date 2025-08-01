package com.sakhura.climaapp2

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sakhura.climaapp2.adapter.PronosticoAdapter
import com.sakhura.climaapp2.model.DiaPronostico
import com.sakhura.climaapp2.repository.ClimaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PronosticoActivity : AppCompatActivity() {

    private lateinit var tvTituloPronostico: TextView
    private lateinit var rvPronostico: RecyclerView
    private lateinit var climaRepository: ClimaRepository
    private var ciudadNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronostico)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutPronostico)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        obtenerDatosIntent()
        cargarPronostico()
    }

    private fun initViews() {
        tvTituloPronostico = findViewById(R.id.tvTituloPronostico)
        rvPronostico = findViewById(R.id.rvPronostico)
        climaRepository = ClimaRepository()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pronóstico 5 días"
    }

    private fun setupRecyclerView() {
        rvPronostico.layoutManager = LinearLayoutManager(this)
    }

    private fun obtenerDatosIntent() {
        ciudadNombre = intent.getStringExtra("CIUDAD_NOMBRE") ?: ""
        if (ciudadNombre.isEmpty()) {
            tvTituloPronostico.text = "Error: Ciudad no especificada"
        } else {
            tvTituloPronostico.text = "Cargando pronóstico para $ciudadNombre..."
        }
    }

    private fun cargarPronostico() {
        if (ciudadNombre.isEmpty()) {
            Toast.makeText(this, "Ciudad no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d("PronosticoActivity", "Obteniendo pronóstico para: $ciudadNombre")

                val pronosticoResponse = climaRepository.obtenerPronostico(ciudadNombre)
                Log.d("PronosticoActivity", "Datos recibidos: ${pronosticoResponse.list.size} registros")

                // Filtrar pronósticos por día (uno por día)
                val pronosticosFiltrados = filtrarPronosticosPorDia(pronosticoResponse.list)
                Log.d("PronosticoActivity", "Pronósticos filtrados: ${pronosticosFiltrados.size} días")

                if (pronosticosFiltrados.isEmpty()) {
                    tvTituloPronostico.text = "No se encontraron pronósticos disponibles"
                    Toast.makeText(this@PronosticoActivity, "No hay datos de pronóstico disponibles", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Configurar adapter
                val adapter = PronosticoAdapter(pronosticosFiltrados)
                rvPronostico.adapter = adapter

                // Actualizar título
                tvTituloPronostico.text = "Pronóstico de ${pronosticosFiltrados.size} días para $ciudadNombre"

                Toast.makeText(this@PronosticoActivity, "Pronóstico cargado exitosamente", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.e("PronosticoActivity", "Error al cargar pronóstico", e)
                tvTituloPronostico.text = "Error al cargar pronóstico"
                
                val errorMessage = when {
                    e.message?.contains("Sin conexión") == true -> "Sin conexión a internet"
                    e.message?.contains("404") == true -> "Ciudad no encontrada"
                    e.message?.contains("401") == true -> "Error de autenticación API"
                    else -> "Error: ${e.message}"
                }
                
                Toast.makeText(this@PronosticoActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun filtrarPronosticosPorDia(pronosticos: List<DiaPronostico>): List<DiaPronostico> {
        val pronosticosFiltrados = mutableMapOf<String, DiaPronostico>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (pronostico in pronosticos) {
            try {
                val fechaHora = pronostico.dt_txt.split(" ")
                val fecha = fechaHora[0]
                val hora = fechaHora[1]

                // Si no existe pronóstico para esta fecha, agregarlo
                if (!pronosticosFiltrados.containsKey(fecha)) {
                    pronosticosFiltrados[fecha] = pronostico
                } else {
                    // Mantener el pronóstico más cercano al mediodía (12:00)
                    val horaActual = hora.substring(0, 2).toIntOrNull() ?: 0
                    val diferenciaNoonActual = kotlin.math.abs(horaActual - 12)

                    val pronosticoExistente = pronosticosFiltrados[fecha]!!
                    val horaExistente = pronosticoExistente.dt_txt.split(" ")[1].substring(0, 2).toIntOrNull() ?: 0
                    val diferenciaNoonExistente = kotlin.math.abs(horaExistente - 12)

                    if (diferenciaNoonActual < diferenciaNoonExistente) {
                        pronosticosFiltrados[fecha] = pronostico
                    }
                }
            } catch (e: Exception) {
                Log.w("PronosticoActivity", "Error procesando pronóstico: ${pronostico.dt_txt}", e)
            }
        }

        // Retornar lista ordenada por fecha
        return pronosticosFiltrados.values.sortedBy { it.dt_txt }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
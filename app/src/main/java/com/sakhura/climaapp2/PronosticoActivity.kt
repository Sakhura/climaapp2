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
import androidx.recyclerview.widget.RecyclerView.Adapter
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutpronostico)) { v, insets ->
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
        ciudadNombre = intent.getStringExtra("CIUDAD_NOMBRE") ?: "Ciudad no encontrada"
        tvTituloPronostico.text = "Pronóstico para los próximos 5 días en $ciudadNombre"
    }

    private fun cargarPronostico() {
        if (ciudadNombre.isEmpty() || ciudadNombre == "Ciudad no encontrada") {
            Toast.makeText(this, "Ciudad no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // loading
                tvTituloPronostico.text = "Cargando pronóstico para $ciudadNombre..."
                Log.d("PronosticoActivity", "Obteniendo info de la API")

                val pronosticoResponse = climaRepository.obtenerPronostico(ciudadNombre)
                Log.d("PronosticoActivity", "datos recibidos: ${pronosticoResponse.list.size}")

                // Filtrar pronósticos por día
                val pronosticosFiltrados = filtrarPronosticosPorDia(pronosticoResponse.list)
                Log.d("PronosticoActivity", "datos filtrados: ${pronosticosFiltrados.size} por día")

                if (pronosticosFiltrados.isEmpty()){
                    Log.w("PronosticoActivity", "No se encontraron pronósticos para el día actual")
                    tvTituloPronostico.text = "No se encontraron pronósticos para el día actual"
                    return@launch
                }

                // Configurar adapter
                val adapter = PronosticoAdapter(pronosticosFiltrados)
                rvPronostico.adapter = adapter
                Log.d("PronosticoActivity", "Adapter configurado")

                // Actualizar título
                tvTituloPronostico.text = "Pronóstico de ${pronosticosFiltrados.size} días para $ciudadNombre"

                Toast.makeText(this@PronosticoActivity, "Pronóstico cargado", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                tvTituloPronostico.text = "Error al cargar pronóstico"
                Toast.makeText(
                    this@PronosticoActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun filtrarPronosticosPorDia(pronosticos: List<DiaPronostico>): List<DiaPronostico> {
        val pronosticosFiltrados = mutableMapOf<String, DiaPronostico>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        for (pronostico in pronosticos) {
            try {
                val dateTime = pronostico.dt_txt.split(" ")
                val fecha = dateTime[0]
                val hora = dateTime[1]

                if (!pronosticosFiltrados.containsKey(fecha)) {
                    pronosticosFiltrados[fecha] = pronostico
                } else {
                    // Keep the forecast closest to noon (12:00)
                    val currentTime = timeFormat.parse(hora)
                    val currentNoonDiff = Math.abs(currentTime.hours - 12)

                    val existingForecast = pronosticosFiltrados[fecha]!!
                    val existingTime = timeFormat.parse(existingForecast.dt_txt.split(" ")[1])
                    val existingNoonDiff = Math.abs(existingTime.hours - 12)

                    if (currentNoonDiff < existingNoonDiff) {
                        pronosticosFiltrados[fecha] = pronostico
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return pronosticosFiltrados.values.toList()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
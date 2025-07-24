package com.sakhura.climaapp2

import android.util.Log.e
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakhura.climaapp2.adapter.PronosticoAdapter
import com.sakhura.climaapp2.model.DiaPronostico
import com.sakhura.climaapp2.repository.ClimaRepository

class PronosticoActivity : AppCompatActivity(){

    private lateinit vat tvTituloPronostico: TextView
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

        private fun initViews() {
            tvTituloPronostico = findViewById(R.id.tvTituloPronostico)
            rvPronostico = findViewById(R.id.rvPronostico)
            climaRepository = ClimaRepository()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Pronostico 5 días"
        }

        private fun setupRecyclerView() {
            rvPronostico.layoutManager = LinearLayoutManager(this)

    }
        private fun obtenerDatosIntent() {
            ciudadNombre = intent.getStringExtra("ciudadNombre") ?: "Ciudad no encontrada"
            tvTituloPronostico.text = "Pronóstico para los proximos 5 días en $ciudadNombre"
        }

        private fun cargarPronostico() {
        if(ciudadNombre.isNotEmpty() || ciudadNombre != "Ciudad no encontrada"){
            Toast.makeText(this, "Ciudad no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
            CoroutineScope(Dispatchers.Main).launch {
                try {
                }
                // loading
                tvTituloPronostico.text = "Pronóstico para los proximos 5 días en $ciudadNombre"
                    val pronosticoResponse = climaRepository.obtenerPronostico(ciudadNombre)
                    //filtro 1
                    val pronosticosFiltrados = filtrarPronosticosPordia(pronosticoResponse.list)
                    //adapter
                    val adapter = PronosticoAdapter(pronosticos)
                    rvPronostico.adapter = adapter
                    //actualiza loading
                    tvTituloPronostico.text = "Pronóstico de ${pronosticosFiltrados.size} para $ciudadNombre"

                Toast.makeText(this@PronosticoActivity, "Pronostico cargado", Toast.LENGTH_SHORT).show())
            }   catch (e: Exception) {
                tvTituloPronostico.text = "Pronostico Cargado Exitosamente", Toast.LENGTH_SHORT).show()

                }
            }

    }

    private fun filtrarPronosticosPordia(pronosticos: List<DiaPronostico>): List<DiaPronostico> {
        val pronosticosFiltrados = mutableMapOf<String, DiaPronostico>()

        for (pronostico in lista) {
            val fecha = pronostico.dt_txt.substring(0, 10) // Obtener la fecha (sin la hora)
            val hora = pronostico.dt_txt.substring(11, 16) // Obtener la hora

            if (!pronosticosFiltrados.containsKey(fecha)) ||
                    kotlin.math.abs(hora - 12) < kotlin.math.abs(
                filtrarPronosticosPordia[fecha]!!.dt_txt.substring(
                    11,
                    16
                ).toInt() - 12
            ) {
                filtrarPronosticosPordia()
            }

        } return pronosticosFiltrados.values.toList()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}


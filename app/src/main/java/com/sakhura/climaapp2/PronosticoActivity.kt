package com.sakhura.climaapp2

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakhura.climaapp2.adapter.PronosticoAdapter
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
                    tvTituloPronostico.text = "Pronóstico para los proximos 5 días en $ciudadNombre"
                    val pronosticoResponse = climaRepository.obtenerPronostico(ciudadNombre)
                    val pronosticosFiltrados = filtrarPronosticosPordia(pronosticoResponse.list)
                    val adapter = PronosticoAdapter(pronosticos)
                    rvPronostico.adapter = adapter
    /*
    * filtar pronosticos
    * val adapter
    * fun filtrarPronosticosPordia
    * */
                }
            }

    }

}
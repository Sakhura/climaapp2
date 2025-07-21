package com.sakhura.climaapp2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sakhura.climaapp2.repository.ClimaRepository

class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding
     private val ClimaRepository = ClimaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBuscar.setOnClickListener {
            val ciudad = binding.etCiudad.text.toString()
            if (ciudad.isNotBlank()) {
                obtenerClima(ciudad)

        } else{
          //  binding.tvCiudad.text = "Ciudad"
           // binding.tvDescripcion.text = "Descripcion"
           // binding.tvTemperatura.text = "Temperatura"
                Toast.makeText(this, "Ingrese Ciudad", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnUbicacion.setOnClickListener {
            solicitarPermisosUbicacion()
        }

    }
}
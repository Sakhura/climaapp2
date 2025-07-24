package com.sakhura.climaapp2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sakhura.climaapp2.databinding.ActivityMainBinding
import com.sakhura.climaapp2.repository.ClimaRepository
import com.sakhura.climaapp2.utils.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val climaRepository = ClimaRepository()
    private val PERMISSIONS_REQUEST_LOCATION = 100
    private var ultimaCiudadConsultada = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBuscar.setOnClickListener {
            val ciudad = binding.etCiudad.text.toString()
            if (ciudad.isNotBlank()) {
                obtenerClima(ciudad)
            } else {
                Toast.makeText(this, "Ingrese Ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUbicacion.setOnClickListener {
            solicitarPermisosUbicacion()
        }

        // Agregar click listener para abrir pronóstico
        binding.tvCiudad.setOnClickListener {
            if (ultimaCiudadConsultada.isNotEmpty()) {
                abrirPronostico(ultimaCiudadConsultada)
            }
        }
    }

    private fun obtenerClima(ciudad: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val climaResponse = climaRepository.obtenerClima(ciudad)
                binding.tvCiudad.text = climaResponse.nombre
                binding.tvDescripcion.text = climaResponse.weather[0].description
                binding.tvTemperatura.text = "${climaResponse.main.temp.toInt()}°C"
                ultimaCiudadConsultada = climaResponse.nombre
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error al obtener el clima: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun abrirPronostico(ciudad: String) {
        val intent = Intent(this, PronosticoActivity::class.java)
        intent.putExtra("CIUDAD_NOMBRE", ciudad)
        startActivity(intent)
    }

    private fun solicitarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            obtenerUbicacion()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
        } else {
            Toast.makeText(
                this,
                "Permiso de ubicacion requerido para obtener clima actual",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun obtenerUbicacion() {
        LocationHelper.obtenerUbicacion(this) { location ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                try {
                    val direcciones =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val ciudad = direcciones?.firstOrNull()?.locality ?: "Ciudad no encontrada"
                    if (ciudad != "Ciudad no encontrada") {
                        obtenerClima(ciudad)
                        binding.etCiudad.setText(ciudad)
                    } else {
                        binding.tvCiudad.text = "❌ Ciudad no encontrada"
                        binding.tvTemperatura.text = " --°C"
                        binding.tvDescripcion.text = "Busca una ciudad para ver el clima"
                        Toast.makeText(
                            this,
                            "Error al obtener nombre de la ciudad",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    binding.tvCiudad.text = "❌ Error de geolocalizacion"
                    binding.tvTemperatura.text = " --°C"
                    binding.tvDescripcion.text = "Error al obtener la ciudad donde te encuentras"
                    Toast.makeText(this, "Error al obtener nombre de la ciudad", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.tvCiudad.text = " Sin Ubicacion"
                binding.tvTemperatura.text = " --°C"
                binding.tvDescripcion.text = "No se puede obtener ubicación"
                Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
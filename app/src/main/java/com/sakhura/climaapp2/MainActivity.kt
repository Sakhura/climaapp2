package com.sakhura.climaapp2

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sakhura.climaapp2.model.Ciudad
import com.sakhura.climaapp2.repository.ClimaRepository
import com.sakhura.climaapp2.utils.LocationHelper
import com.sakhura.climaapp2.utils.LocationHelper.obtenerUbicacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.jar.Manifest

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

            } else {
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

    private fun obtenerClima(ciudad: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val climaResponse = ClimaRepository.obtenerClima(ciudad)
                binding.tvCiudad.text = climaResponse.nombre
                binding.tvDescripcion.text = climaResponse.weather[0].description
                binding.tvTemperatura.text = "${climaResponse.main.temp}°C"
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al obtener el clima", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        }
    private fun solicitarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)
        } else {
            obtenerUbicacion()
        }
    }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }

             }

            private fun obtenerUbicacion() {
                LocationHelper.obtenerUbicacion(this) { location ->
                    if (location != null) {
                       val geocoder = Geocoder(this, Locale.getDefault())
                        val direccion= geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val ciudad = direccion?.firstOrNull()?.locality ?: "Ciudad"
                        if (!ciudad.isNullOrBlank()) {
                            obtenerClima(ciudad)
                            binding.tvCiudad.setText(Ciudad)
                        }
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                    }
                }

            }

    }
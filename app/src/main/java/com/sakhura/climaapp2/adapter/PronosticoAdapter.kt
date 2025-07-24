package com.sakhura.climaapp2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sakhura.climaapp2.R
import com.sakhura.climaapp2.model.DiaPronostico
import java.text.SimpleDateFormat
import java.util.Locale

class PronosticoAdapter(private val pronosticos: List<DiaPronostico>) :
    RecyclerView.Adapter<PronosticoAdapter.PronosticoViewHolder>() {

    class PronosticoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcono: ImageView = itemView.findViewById(R.id.ivIconoClima)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionPronostico)
        val tvTemperatura: TextView = itemView.findViewById(R.id.tvTemperaturaPronostico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PronosticoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dia_pronostico, parent, false)
        return PronosticoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PronosticoViewHolder, position: Int) {
        val pronostico = pronosticos[position]

        // Fecha
        val fechaFormateada = formatearFecha(pronostico.dt_txt)
        holder.tvFecha.text = fechaFormateada

        // Descripción clima
        holder.tvDescripcion.text = pronostico.weather[0].description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        // Temperatura
        holder.tvTemperatura.text = "${pronostico.main.temp.toInt()}°C"

        // Emoji del clima
        val emoji = obtenerEmojiClima(pronostico.weather[0].main)
        holder.ivIcono.contentDescription = emoji

    }

    override fun getItemCount(): Int = pronosticos.size

    private fun formatearFecha(fechaString: String): String {
        return try {
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaString)
            val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatoSalida.format(fecha!!)
        } catch (e: Exception) {
            fechaString.substring(0, 10)
        }
    }

    private fun obtenerEmojiClima(condicionClima: String): String {
        return when (condicionClima.lowercase()) {
            "clear" -> "☀️"
            "clouds" -> "☁️"
            "rain" -> "🌧️"
            "drizzle" -> "🌦️"
            "thunderstorm" -> "⛈️"
            "snow" -> "❄️"
            "mist", "fog" -> "🌫️"
            else -> "🌤️"
        }
    }
}
package com.sakhura.climaapp2.adapter

import com.sakhura.climaapp2.model.DiaPronostico
import java.text.SimpleDateFormat
import java.util.Locale

class PronosticoAdapter (private val pronosticos: List<DiaPronostico>):
        RecyclerView.Adapter<PronosticoAdapter.ViewHolder>() {

            Class PronosticoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
                val ivIcono: ImageView = itemView.findViewById(R.id.ivIcono)
                val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
                val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
                val tvTemperatura: TextView = itemView.findViewById(R.id.tvTemperatura)
            }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PronosticoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pronostico, parent, false)
            return PronosticoViewHolder(view)

            //fecha
            val fechaFormateada = formatoFecha.format(pronostico.dt_txt)
            holder.tvFecha.text = fechaFormateada

            //descripcion clima
            holder.tvDescripcionPronostico.text = pronostico.weather[0].description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            //temperatura
            holder.tvTemperaturaPronostico.text = "${pronostico.main.temp.toInt()}°C"

            val emoji = obtenerEmojiClima(pronostico.weather[0].main)
            holder.ivIconoCkima.contentDescription = emoji

        }

    override fun getItemCount(): In = pronosticos.size

    private fun formatearFecha(fechaString: String): String{
        return try {
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaStr)
            val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatoSalida.format(fecha)
        } catch (e: Exception) {
            fechaStr.substring(0, 10)
        }
    }

    private fun obtenerEmojiClima(descripcionClima: String): String {
        return when ((condicionClima.lowercase()){
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
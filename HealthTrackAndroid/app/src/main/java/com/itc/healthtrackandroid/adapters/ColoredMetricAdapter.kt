package com.itc.healthtrackandroid.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itc.healthtrackandroid.R
import com.itc.healthtrackandroid.models.Metric
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adaptador con indicadores de color clinico para cada fila de metrica.
 * Usado tanto en la vista del doctor como en el historial del paciente.
 *
 * Rojo  (#4A1010): PA sistolica > 140, diastolica > 90 o glucosa > 200
 * Ambar (#3A2E00): IMC > 30 (sin alerta roja)
 * Verde (#0D2B0D): valores dentro del rango normal
 */
class ColoredMetricAdapter(
    private val metrics: MutableList<Metric>
) : RecyclerView.Adapter<ColoredMetricAdapter.MetricViewHolder>() {

    /**
     * Reemplaza los datos del adaptador y notifica al RecyclerView para que redibuje la lista.
     * Se llama desde HistoryActivity cada vez que Firestore emite una actualizacion.
     */
    fun updateData(newMetrics: List<Metric>) {
        metrics.clear()
        metrics.addAll(newMetrics)
        notifyDataSetChanged()
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class MetricViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorStripe: View      = itemView.findViewById(R.id.metricColorStripe)
        val dateTextView: TextView = itemView.findViewById(R.id.metricDateTextView)
        val bpTextView: TextView   = itemView.findViewById(R.id.metricBpTextView)
        val heartRateTextView: TextView = itemView.findViewById(R.id.metricHeartRateTextView)
        val glucoseTextView: TextView   = itemView.findViewById(R.id.metricGlucoseTextView)
        val bmiTextView: TextView       = itemView.findViewById(R.id.metricBmiTextView)
        val notesTextView: TextView     = itemView.findViewById(R.id.metricNotesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetricViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_metric_colored, parent, false)
        return MetricViewHolder(view)
    }

    override fun onBindViewHolder(holder: MetricViewHolder, position: Int) {
        val metric = metrics[position]

        // Copiamos las propiedades var a val locales para permitir el smart cast de Kotlin
        val timestamp = metric.timestamp
        val glucoseLevel = metric.glucoseLevel
        val bmi = metric.bmi
        val sys = metric.systolic
        val dia = metric.diastolic

        // Fecha del registro
        holder.dateTextView.text = if (timestamp != null) {
            dateFormat.format(timestamp.toDate())
        } else "—"

        // Presion arterial
        holder.bpTextView.text = if (sys != null && dia != null) {
            "PA: $sys/$dia mmHg"
        } else "PA: —"

        // Frecuencia cardiaca
        holder.heartRateTextView.text = if (metric.heartRate != null) {
            "FC: ${metric.heartRate} lpm"
        } else "FC: —"

        // Glucosa
        holder.glucoseTextView.text = if (glucoseLevel != null) {
            "Glucosa: $glucoseLevel mg/dL"
        } else "Glucosa: —"

        // IMC calculado automaticamente al guardar la metrica
        holder.bmiTextView.text = if (bmi != null) {
            "IMC: $bmi"
        } else "IMC: —"

        // Notas: solo se muestran si el paciente las escribio
        val notes = metric.notes
        if (!notes.isNullOrBlank()) {
            holder.notesTextView.visibility = View.VISIBLE
            holder.notesTextView.text = "Notas: $notes"
        } else {
            holder.notesTextView.visibility = View.GONE
        }

        // Franja lateral de color segun los umbrales clinicos.
        // El fondo de la fila se mantiene oscuro fijo; solo la franja cambia de color.
        val stripeColor = when {
            (sys != null && sys > 140) ||
            (dia != null && dia > 90) ||
            (glucoseLevel != null && glucoseLevel > 200.0) ->
                Color.parseColor("#C0392B") // rojo — riesgo alto

            bmi != null && bmi > 30.0 ->
                Color.parseColor("#E6A817") // ambar — IMC elevado

            else ->
                Color.parseColor("#2E7D32") // verde — normal
        }
        holder.colorStripe.setBackgroundColor(stripeColor)
        holder.itemView.setBackgroundColor(Color.parseColor("#152231")) // fondo fijo
    }

    override fun getItemCount(): Int = metrics.size
}

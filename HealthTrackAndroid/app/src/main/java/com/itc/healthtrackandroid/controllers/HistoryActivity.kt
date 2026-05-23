package com.itc.healthtrackandroid.controllers

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.itc.healthtrackandroid.R
import com.itc.healthtrackandroid.adapters.ColoredMetricAdapter
import com.itc.healthtrackandroid.dao.GenericDAO
import com.itc.healthtrackandroid.models.Metric

/**
 * Historial de metricas del paciente.
 * Usa un listener en tiempo real para actualizarse automaticamente con cada nuevo registro.
 * Cada fila muestra fecha, PA, FC, glucosa, IMC y notas, con color clinico de fondo.
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var metricDao: GenericDAO<Metric>

    // Referencia al listener en tiempo real — se cancela en onDestroy para evitar fugas de memoria
    private var metricsListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        auth = FirebaseAuth.getInstance()
        metricDao = GenericDAO(Metric::class.java, "metrics")

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        startListeningMetrics()
    }

    /**
     * Registra un listener en tiempo real en Firestore.
     * El adaptador se actualiza automaticamente en cada cambio, sin necesidad de recargar manualmente.
     */
    private fun startListeningMetrics() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        metricsListener = metricDao.listenByField("patientId", currentUserId) { metrics ->
            if (metrics.isEmpty()) {
                Toast.makeText(this, "Sin registros aún", Toast.LENGTH_SHORT).show()
            } else {
                // Ordenamos de mas reciente a mas antiguo
                val sortedList = metrics.sortedByDescending { it.timestamp }
                // ColoredMetricAdapter muestra todos los campos con indicadores de color clinico
                historyRecyclerView.adapter = ColoredMetricAdapter(sortedList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelamos el listener cuando la actividad se destruye — evita actualizaciones
        // que lleguen despues de que el RecyclerView ya no exista
        metricsListener?.remove()
        metricsListener = null
    }
}

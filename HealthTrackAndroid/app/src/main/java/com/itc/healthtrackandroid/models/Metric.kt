package com.itc.healthtrackandroid.models

import com.google.firebase.Timestamp

/**
 * Representa una medicion individual registrada por el paciente.
 */
data class Metric(
    // Identificador unico de la medicion
    var id: String? = null,

    // Identificador del paciente al que pertenece esta medicion
    var patientId: String = "",

    // Fecha y hora exacta de la medicion. Importante usar el Timestamp de Firebase.
    var timestamp: Timestamp? = null,

    // Tipo de metrica (presion, glucosa, peso, etc.)
    var metricType: String? = null,

    // Presion arterial sistolica (el numero de arriba)
    var systolic: Int? = null,

    // Presion arterial diastolica (el numero de abajo)
    var diastolic: Int? = null,

    // Frecuencia cardiaca en latidos por minuto
    var heartRate: Int? = null,

    // Peso en kilogramos
    var weight: Double? = null,

    // Indice de Masa Corporal calculado
    var bmi: Double? = null,

    // Nivel de glucosa en la sangre
    var glucoseLevel: Double? = null
)
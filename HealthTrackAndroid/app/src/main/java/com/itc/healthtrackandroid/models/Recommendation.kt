package com.itc.healthtrackandroid.models

import com.google.firebase.Timestamp

/**
 * Representa una recomendacion o alerta medica generada para un paciente.
 */
data class Recommendation(
    // Identificador unico de la recomendacion
    var id: String? = null,

    // Identificador del paciente que recibe la alerta
    var patientId: String = "",

    // Identificador del doctor que genero la recomendacion
    var doctorId: String? = null,

    // Fecha y hora en la que se creo la alerta
    var generatedAt: Timestamp? = null,

    // Tipo de alerta (ejemplo: "alerta", "sugerencia")
    var type: String = "",

    // Titulo principal de la recomendacion
    var title: String = "",

    // Mensaje detallado con el analisis clinico
    var message: String = "",

    // Indica si el paciente ya leyo esta notificacion
    var isRead: Boolean = false
)
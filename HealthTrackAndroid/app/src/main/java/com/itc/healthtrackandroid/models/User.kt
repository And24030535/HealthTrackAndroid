package com.itc.healthtrackandroid.models

/**
 * Representa a cualquier usuario del sistema (paciente, doctor o administrador).
 * Se asignan valores por defecto (como = "" o = null) para que Firebase pueda
 * crear el objeto automaticamente al descargar datos de internet.
 */
data class User(
    // Identificador unico del usuario
    var uid: String? = null,

    // Correo electronico para iniciar sesion
    var email: String = "",

    // Nombre de pila
    var firstName: String = "",

    // Apellidos
    var lastName: String = "",

    // Rol del usuario: "patient", "doctor", o "admin"
    var role: String = "",

    // Fecha de nacimiento guardada como texto
    var birthDate: String? = null,

    // Genero del usuario ("M", "F", "Other")
    var gender: String? = null,

    // Estatura en metros (ejemplo: 1.75). Double se usa para decimales.
    var height: Double? = null,

    // El ID del medico que atiende a este paciente (solo util si el rol es "patient")
    var assignedDoctorId: String? = null,

    // El nombre del medico asignado, para mostrarlo facilmente en la pantalla
    var assignedDoctorName: String? = null,
    
    // Lista de IDs de pacientes asignados (solo util si el rol es "doctor")
    var patientIds: List<String>? = null
)
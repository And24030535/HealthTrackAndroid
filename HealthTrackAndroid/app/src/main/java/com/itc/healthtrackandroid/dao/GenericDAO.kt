package com.itc.healthtrackandroid.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * Clase generica para manejar la base de datos.
 * Funciona para cualquier modelo de datos sin tener que repetir el codigo.
 * * @param entityClass El tipo de clase que vamos a manejar (ej. User::class.java)
 * @param collectionName El nombre de la carpeta en Firebase (ej. "users", "metrics")
 */
class GenericDAO<T : Any>(
    private val entityClass: Class<T>,
    private val collectionName: String
) {
    // Instancia de la conexion a la base de datos Firestore
    private val db = FirebaseFirestore.getInstance()

    /**
     * Genera un nuevo ID aleatorio directamente desde Firebase.
     * Util para crear nuevos registros sin que se repitan los identificadores.
     */
    fun createDocumentId(): String {
        return db.collection(collectionName).document().id
    }

    /**
     * Guarda o actualiza un documento en la base de datos.
     */
    fun save(documentId: String, entity: T, callback: OnOperationCompleteListener) {
        db.collection(collectionName).document(documentId).set(entity)
            // Si la operacion es exitosa, avisamos a la pantalla
            .addOnSuccessListener { callback.onSuccess() }
            // Si falla, pasamos el error para que la pantalla decida que hacer
            .addOnFailureListener { exception -> callback.onFailure(exception) }
    }

    /**
     * Busca un solo elemento usando su ID especifico.
     */
    fun getById(documentId: String, callback: OnSingleDataLoadedListener<T>) {
        db.collection(collectionName).document(documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Convierte el documento de internet en un objeto de Kotlin
                    val entity = document.toObject(entityClass)
                    callback.onSuccess(entity)
                } else {
                    // Si no existe, devuelve nulo
                    callback.onSuccess(null)
                }
            }
            .addOnFailureListener { exception -> callback.onFailure(exception) }
    }

    /**
     * Registra un listener en tiempo real para documentos donde campo == valor.
     * El callback se invoca cada vez que Firestore detecta cambios, incluyendo la primera lectura.
     * El caller debe cancelar con .remove() en onDestroy() para evitar fugas de memoria.
     */
    fun listenByField(
        fieldName: String,
        value: Any,
        callback: OnDataLoadedListener<T>
    ): ListenerRegistration {
        return db.collection(collectionName)
            .whereEqualTo(fieldName, value)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    callback.onFailure(error)
                    return@addSnapshotListener
                }
                if (snapshots == null) return@addSnapshotListener
                val list = mutableListOf<T>()
                for (doc in snapshots) {
                    val entity = doc.toObject(entityClass)
                    list.add(entity)
                }
                callback.onSuccess(list)
            }
    }
}
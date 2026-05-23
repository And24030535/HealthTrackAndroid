package com.itc.healthtrackandroid.dao

/**
 * Interfaz usada cuando la accion no devuelve datos (ejemplo: guardar o borrar).
 */
interface OnOperationCompleteListener {
    // Funcion que se ejecuta si todo salio bien
    fun onSuccess()
    // Funcion que se ejecuta si ocurrio un error (sin internet, sin permisos, etc.)
    fun onFailure(error: Exception)
}

/**
 * Interfaz usada cuando pedimos una lista completa de datos (ejemplo: todo el historial).
 * La letra T significa "Cualquier Tipo" (puede ser User, Metric o Recommendation).
 */
interface OnDataLoadedListener<T> {
    // Devuelve una lista de los objetos solicitados
    fun onSuccess(data: List<T>)
    fun onFailure(error: Exception)
}

/**
 * Interfaz usada cuando buscamos un solo documento en especifico por su ID.
 */
interface OnSingleDataLoadedListener<T> {
    // Devuelve un solo objeto, o nulo si no lo encontro
    fun onSuccess(data: T?)
    fun onFailure(error: Exception)
}
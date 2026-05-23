package com.itc.healthtrackandroid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.itc.healthtrackandroid.services.NotificationHelper

/**
 * Receptor que el sistema activa cuando llega la hora del recordatorio programado.
 * Su unica responsabilidad es mostrar la notificacion de recordatorio.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // cuando suena la alarma mostramos la notificacion de recordatorio al paciente
        NotificationHelper.showReminderNotification(context)
    }
}

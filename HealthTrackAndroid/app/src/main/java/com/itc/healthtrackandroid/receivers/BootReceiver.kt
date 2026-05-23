package com.itc.healthtrackandroid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.itc.healthtrackandroid.services.ReminderScheduler

/**
 * Receptor que se activa cuando el dispositivo termina de encenderse.
 * Los AlarmManager se borran al reiniciar el telefono, por lo que este receiver
 * se encarga de reprogramar el recordatorio diario automaticamente.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val savedTime = ReminderScheduler.getSavedTime(context)
            if (savedTime != null) {
                ReminderScheduler.schedule(context, savedTime.first, savedTime.second)
            }
        }
    }
}

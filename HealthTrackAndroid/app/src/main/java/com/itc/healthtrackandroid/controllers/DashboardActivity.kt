package com.itc.healthtrackandroid.controllers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.itc.healthtrackandroid.R

/**
 * Panel Principal del paciente.
 * Permite navegar a registrar metricas, ver el historial o cerrar sesion.
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var addMetricButton: Button
    private lateinit var viewHistoryButton: Button
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        welcomeTextView = findViewById(R.id.welcomeTextView)
        addMetricButton = findViewById(R.id.addMetricButton)
        viewHistoryButton = findViewById(R.id.viewHistoryButton)
        logoutButton = findViewById(R.id.logoutButton)

        val userName = intent.getStringExtra("USER_NAME") ?: ""
        welcomeTextView.text = if (userName.isNotEmpty())
            "Bienvenido, $userName\nPaciente"
        else
            "Bienvenido a HealthTrack"

        addMetricButton.setOnClickListener {
            startActivity(Intent(this, AddMetricActivity::class.java))
        }

        viewHistoryButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        logoutButton.setOnClickListener { performLogout() }
    }

    /**
     * Cierra la sesion en Firebase y vuelve a la pantalla de inicio de sesion.
     */
    private fun performLogout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

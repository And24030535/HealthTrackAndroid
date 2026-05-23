package com.itc.healthtrackandroid.services

import android.util.Base64
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.net.ssl.SSLSocketFactory

/**
 * Servicio para enviar correos electronicos via Gmail SMTP sobre SSL (puerto 465).
 * Usa unicamente APIs de Android integradas — sin dependencias externas.
 * El envio se ejecuta en un hilo secundario para no bloquear la pantalla.
 */
object EmailService {

    private const val SMTP_EMAIL    = "clinicahealthtrack@gmail.com"
    private const val SMTP_PASSWORD = "yaih bgnl dubi ctgs"
    private const val SMTP_HOST     = "smtp.gmail.com"
    private const val SMTP_PORT     = 465  // SMTPS — SSL directo sin STARTTLS

    /**
     * Envia un correo electronico en segundo plano.
     *
     * @param toEmail   Destinatario
     * @param subject   Asunto del correo
     * @param body      Cuerpo del correo (texto plano)
     * @param onSuccess Se invoca si el envio fue exitoso
     * @param onFailure Se invoca con la excepcion si fallo
     */
    fun send(
        toEmail: String,
        subject: String,
        body: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Thread {
            try {
                // Conexion SSL directa — SSLSocketFactory es parte del JDK/Android
                val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
                val socket  = factory.createSocket(SMTP_HOST, SMTP_PORT)

                val reader = BufferedReader(InputStreamReader(socket.inputStream,  Charsets.UTF_8))
                val writer = BufferedWriter(OutputStreamWriter(socket.outputStream, Charsets.UTF_8))

                // Envia un comando SMTP y vuelca el flujo en el buffer de salida
                fun cmd(line: String) {
                    writer.write("$line\r\n")
                    writer.flush()
                }

                // Lee lineas del servidor hasta que el codigo de respuesta ya no tenga guion
                // (formato multi-linea: "250-..." ... "250 OK")
                fun read(): String {
                    val sb = StringBuilder()
                    var line: String?
                    do {
                        line = reader.readLine()
                        if (line != null) sb.appendLine(line)
                    } while (line != null && line.length > 3 && line[3] == '-')
                    return sb.toString()
                }

                // Codifica una cadena en Base64 sin saltos de linea (requerido por AUTH LOGIN)
                fun b64(value: String): String =
                    Base64.encodeToString(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                // Contrasena sin espacios (el App Password de Google tiene espacios decorativos)
                val passwordClean = SMTP_PASSWORD.replace(" ", "")

                // Dialogo SMTP
                read()                                          // 220 smtp.gmail.com ...
                cmd("EHLO healthtrack.app");  read()           // 250 capabilities
                cmd("AUTH LOGIN");            read()           // 334 VXNlcm5hbWU6
                cmd(b64(SMTP_EMAIL));         read()           // 334 UGFzc3dvcmQ6
                cmd(b64(passwordClean))
                val authResponse = read()                      // 235 2.7.0 Accepted
                if (!authResponse.trimStart().startsWith("2"))
                    throw Exception("Error de autenticacion SMTP: $authResponse")

                cmd("MAIL FROM:<$SMTP_EMAIL>"); read()
                cmd("RCPT TO:<$toEmail>");      read()
                cmd("DATA");                    read()         // 354 Start input

                // Cabeceras del mensaje
                cmd("From: HealthTrack <$SMTP_EMAIL>")
                cmd("To: $toEmail")
                cmd("Subject: $subject")
                cmd("MIME-Version: 1.0")
                cmd("Content-Type: text/plain; charset=UTF-8")
                cmd("")  // linea en blanco separa cabeceras del cuerpo

                // Cuerpo — "dot stuffing": si una linea comienza con punto se duplica
                body.lines().forEach { line ->
                    cmd(if (line.startsWith(".")) ".$line" else line)
                }

                cmd(".");   read()  // 250 Message accepted
                cmd("QUIT")
                socket.close()

                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }.start()
    }
}

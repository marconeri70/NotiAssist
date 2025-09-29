package it.mario.notiassist.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import it.mario.notiassist.R
import android.content.SharedPreferences
import android.content.Context
import android.app.NotificationManager

class MainActivity : ComponentActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("notiassist_prefs", Context.MODE_PRIVATE)

        val et = findViewById<EditText>(R.id.etApiKey)
        val saved = prefs.getString("openai_key", "") ?: ""
        et.setText(saved)

        findViewById<Button>(R.id.btnSaveKey).setOnClickListener {
            prefs.edit().putString("openai_key", et.text.toString().trim()).apply()
        }

        findViewById<Button>(R.id.btnOpenNotifAccess).setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        findViewById<Button>(R.id.btnTest).setOnClickListener {
            // Placeholder: mostra lo stato accesso notifiche
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val tv = findViewById<TextView>(R.id.tvStatus)
            tv.text = "Pronto. Ricorda di abilitare l'accesso Notifiche a NotiAssist."
        }
    }
}

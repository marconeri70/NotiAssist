package it.mario.notiassist.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import it.mario.notiassist.R

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("notiassist_prefs", Context.MODE_PRIVATE)

        val et = findViewById<EditText>(R.id.etApiKey)
        et.setText(prefs.getString("openai_key", "") ?: "")

        findViewById<Button>(R.id.btnSaveKey).setOnClickListener {
            prefs.edit().putString("openai_key", et.text.toString().trim()).apply()
        }

        findViewById<Button>(R.id.btnOpenNotifAccess).setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        findViewById<Button>(R.id.btnTest).setOnClickListener {
            findViewById<TextView>(R.id.tvStatus).text =
                "Pronto. Ricorda di abilitare l'accesso alle notifiche per NotiAssist."
        }
    }
}


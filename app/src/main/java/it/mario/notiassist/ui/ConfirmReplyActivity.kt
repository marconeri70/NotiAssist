package it.mario.notiassist.ui

import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import it.mario.notiassist.AiClient
import it.mario.notiassist.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmReplyActivity : ComponentActivity() {

    private var actions: Array<Notification.Action?>? = null
    private var idx: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_reply)

        val title = intent.getStringExtra("title") ?: ""
        val preview = intent.getStringExtra("preview") ?: ""
        @Suppress("DEPRECATION")
        actions = intent.getParcelableArrayExtra("actions") as? Array<Notification.Action?>
        idx = intent.getIntExtra("action_index", -1)

        findViewById<TextView>(R.id.tvTitle).text = title
        findViewById<TextView>(R.id.tvPreview).text = preview

        val et = findViewById<EditText>(R.id.etReply)

        // Prende API key da SharedPreferences
        val key = getSharedPreferences("notiassist_prefs", Context.MODE_PRIVATE)
            .getString("openai_key", "") ?: ""

        // Chiede una bozza all'AI (senza lifecycleScope per evitare dipendenza extra)
        if (key.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                val suggestion = AiClient.suggestReply(key, "$title\n$preview")
                runOnUiThread { et.setText(suggestion) }
            }
        }

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            val msg = et.text.toString().trim()
            if (sendInlineReply(msg)) {
                Toast.makeText(this, "Risposta inviata", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Impossibile inviare (notifica non supportata).", Toast.LENGTH_LONG).show()
            }
        }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun sendInlineReply(message: String): Boolean {
        val action = actions?.getOrNull(idx) ?: return false
        val inputs = action.remoteInputs ?: return false
        val pi: PendingIntent = action.actionIntent ?: return false

        val results = android.os.Bundle()
        inputs.forEach { ri -> results.putCharSequence(ri.resultKey, message) }

        val fillIn = android.content.Intent()
        RemoteInput.addResultsToIntent(inputs, fillIn, results)

        return try {
            pi.send(this, 0, fillIn)
            true
        } catch (_: Exception) {
            false
        }
    }
}


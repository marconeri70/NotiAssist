package it.mario.notiassist

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

object AiClient {
    private val http = OkHttpClient()

    fun suggestReply(apiKey: String, context: String): String {
        val sys = "Sei un assistente italiano. Rispondi in 1-2 frasi, educate e pertinenti. Se il messaggio è ambiguo, chiedi cortesemente chiarimenti."
        val user = "Messaggio ricevuto:\n$context\n\nScrivi una risposta adeguata (max 2 frasi)."

        val bodyJson = JSONObject().apply {
            put("model", "gpt-4o-mini")
            put("messages", JSONArray(listOf(
                JSONObject(mapOf("role" to "system", "content" to sys)),
                JSONObject(mapOf("role" to "user",   "content" to user))
            )))
        }.toString()

        val req = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(RequestBody.create("application/json".toMediaType(), bodyJson))
            .build()

        http.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            return try {
                val json = JSONObject(body)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
            } catch (_: Exception) {
                // Bozza neutra in caso di errore API
                "Grazie del messaggio; ti rispondo appena possibile."
            }
        }
    }
}

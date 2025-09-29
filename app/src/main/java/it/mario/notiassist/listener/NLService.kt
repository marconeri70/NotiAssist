package it.mario.notiassist.listener

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import it.mario.notiassist.ui.ConfirmReplyActivity

class NLService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val n = sbn.notification ?: return

        val actions = n.actions ?: return
        var replyIdx = -1
        actions.forEachIndexed { idx, action ->
            val ri = action?.remoteInputs
            if (ri != null && ri.isNotEmpty()) replyIdx = idx
        }
        if (replyIdx < 0) return

        val extras = n.extras
        val title = (extras.getCharSequence(Notification.EXTRA_TITLE) ?: sbn.packageName).toString()
        val text = (extras.getCharSequence(Notification.EXTRA_TEXT) ?: "").toString()
        val lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString("\n") { "$it" } ?: text

        val i = Intent(this, ConfirmReplyActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("title", title)
            putExtra("preview", lines)
            putExtra("actions", n.actions)
            putExtra("action_index", replyIdx)
        }
        startActivity(i)
    }
}

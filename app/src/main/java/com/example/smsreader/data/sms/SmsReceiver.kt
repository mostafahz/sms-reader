package com.example.smsreader.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.displayOriginatingAddress
                val body = message.messageBody
                val timestamp = message.timestampMillis
                
                Log.d("SmsReceiver", "SMS from $sender: $body")
                
                // TODO: Trigger processing logic here (inject repository or use a worker)
                // For now, we just log it. We will connect this to the Repository later.
            }
        }
    }
}

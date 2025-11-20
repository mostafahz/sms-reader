package com.example.smsreader.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.smsreader.SmsReaderApp
import com.example.smsreader.domain.SmsMessage
import com.example.smsreader.domain.TransactionProcessor
import com.example.smsreader.data.AppContainer
import com.example.smsreader.data.repository.SmsRepository

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val pendingResult = goAsync()
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                try {
                    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    if (messages.isNullOrEmpty()) return@launch

                    val app = context.applicationContext as SmsReaderApp
                    val repository = app.container.smsRepository
                    val processor = TransactionProcessor(repository)

                    val smsMessages = messages.mapNotNull {
                        val address = it.displayOriginatingAddress
                        val body = it.messageBody
                        
                        if (address != null && body != null) {
                            SmsMessage(
                                address = address,
                                body = body,
                                date = it.timestampMillis
                            )
                        } else {
                            null
                        }
                    }

                    if (smsMessages.isNotEmpty()) {
                        Log.d("SmsReceiver", "Processing ${smsMessages.size} incoming messages")
                        processor.processSmsList(smsMessages)
                    }
                } catch (e: Exception) {
                    Log.e("SmsReceiver", "Error processing SMS", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

package com.example.smsreader.domain

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.util.Log

data class SmsMessage(
    val address: String,
    val body: String,
    val date: Long
)

class SmsReader(private val context: Context) {

    fun readSms(limit: Int = 10, senderIdFilter: String? = null): List<SmsMessage> {
        val messages = mutableListOf<SmsMessage>()
        val cursor: Cursor? = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE),
            null,
            null,
            Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )

        cursor?.use {
            val addressIndex = it.getColumnIndex(Telephony.Sms.Inbox.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.Inbox.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.Inbox.DATE)

            while (it.moveToNext()) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val date = it.getLong(dateIndex)

                // Filter by Sender ID if provided
                if (senderIdFilter != null && !address.contains(senderIdFilter, ignoreCase = true)) {
                    continue
                }

                messages.add(SmsMessage(address, body, date))

                if (messages.size >= limit) {
                    break
                }
            }
        }
        return messages
    }
}

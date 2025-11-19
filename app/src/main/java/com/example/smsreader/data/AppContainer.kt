package com.example.smsreader.data

import android.content.Context
import com.example.smsreader.data.database.SmsDatabase
import com.example.smsreader.data.repository.SmsRepository

interface AppContainer {
    val smsRepository: SmsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val smsRepository: SmsRepository by lazy {
        SmsRepository(SmsDatabase.getDatabase(context).smsDao())
    }
}

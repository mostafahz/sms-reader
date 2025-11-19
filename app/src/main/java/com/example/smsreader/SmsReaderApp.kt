package com.example.smsreader

import android.app.Application
import com.example.smsreader.data.AppContainer
import com.example.smsreader.data.AppDataContainer

class SmsReaderApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

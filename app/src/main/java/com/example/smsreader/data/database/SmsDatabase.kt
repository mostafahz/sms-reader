package com.example.smsreader.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class, WalletEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
abstract class SmsDatabase : RoomDatabase() {
    abstract fun smsDao(): SmsDao

    companion object {
        @Volatile
        private var Instance: SmsDatabase? = null

        fun getDatabase(context: Context): SmsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SmsDatabase::class.java, "sms_reader_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

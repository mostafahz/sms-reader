package com.example.smsreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val originalSmsBody: String,
    val senderId: String,
    val amount: Double,
    val currency: String,
    val merchantName: String,
    val categoryId: Int,
    val walletId: Int,
    val timestamp: Long
)

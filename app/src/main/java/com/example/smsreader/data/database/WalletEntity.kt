package com.example.smsreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val originalIdentifier: String, // e.g. "Card 1234"
    val userDisplayName: String, // e.g. "My Visa"
    val colorHex: String // e.g. "#FF0000"
)

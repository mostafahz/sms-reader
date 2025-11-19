package com.example.smsreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val iconName: String, // e.g. "ic_food"
    val keywordsJson: String // JSON list of keywords e.g. ["mcdonalds", "kfc"]
)

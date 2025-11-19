package com.example.smsreader.domain

import com.example.smsreader.data.database.CategoryEntity

class CategoryClassifier {

    // Hardcoded default keywords for now. In a real app, these would come from the DB.
    private val defaultCategories = mapOf(
        "Food" to listOf("mcdonalds", "kfc", "burger", "pizza", "restaurant", "cafe", "starbucks", "talabat", "deliveroo"),
        "Transportation" to listOf("uber", "careem", "taxi", "fuel", "adnoc", "enoc", "metro", "nol"),
        "Groceries" to listOf("carrefour", "lulu", "spinneys", "waitrose", "choithrams", "mart", "supermarket"),
        "Health" to listOf("pharmacy", "hospital", "clinic", "doctor", "aster", "life"),
        "Bills" to listOf("du", "etisalat", "dewa", "sewa", "internet", "mobile"),
        "Shopping" to listOf("amazon", "noon", "zara", "hm", "nike", "adidas", "mall")
    )

    fun classify(merchantName: String, categories: List<CategoryEntity>): Int {
        val normalizedMerchant = merchantName.lowercase()

        // 1. Check against DB categories (if they have keywords)
        // For this MVP, we will use the hardcoded map primarily, but ideally we check the DB entities.
        
        for ((categoryName, keywords) in defaultCategories) {
            if (keywords.any { normalizedMerchant.contains(it) }) {
                // Find the matching CategoryEntity ID
                val match = categories.find { it.name.equals(categoryName, ignoreCase = true) }
                if (match != null) {
                    return match.id
                }
            }
        }

        // Default to "Other" or the first category if "Other" not found
        return categories.find { it.name.equals("Other", ignoreCase = true) }?.id ?: categories.firstOrNull()?.id ?: 0
    }
}

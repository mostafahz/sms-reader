package com.example.smsreader.domain

import java.util.regex.Pattern

data class ParsedTransaction(
    val amount: Double,
    val currency: String,
    val merchantName: String,
    val cardIdentifier: String?
)

class TransactionParser {

    // Common patterns for banks (Generic examples)
    // 1. "Purchase of AED 50.00 at STARBUCKS with Card 1234"
    // 2. "AED 120.50 spent on Card 8888 at UBER"
    private val patterns = listOf(
        // Pattern 1: Purchase of [CURRENCY] [AMOUNT] at [MERCHANT] with [CARD]
        Pattern.compile("Purchase of\\s+([A-Z]{3})\\s+(\\d+(?:\\.\\d{1,2})?)\\s+at\\s+(.+?)\\s+with\\s+(?:Card|Account)\\s+(.+)", Pattern.CASE_INSENSITIVE),
        
        // Pattern 2: [CURRENCY] [AMOUNT] spent on [CARD] at [MERCHANT]
        Pattern.compile("([A-Z]{3})\\s+(\\d+(?:\\.\\d{1,2})?)\\s+spent\\s+on\\s+(?:Card|Account)\\s+(.+?)\\s+at\\s+(.+)", Pattern.CASE_INSENSITIVE),
        
        // Pattern 3: Payment of [AMOUNT] [CURRENCY] to [MERCHANT]
        Pattern.compile("Payment of\\s+(\\d+(?:\\.\\d{1,2})?)\\s+([A-Z]{3})\\s+to\\s+(.+?)(?:\\s+on\\s+|$)", Pattern.CASE_INSENSITIVE)
    )

    fun parse(body: String): ParsedTransaction? {
        for (pattern in patterns) {
            val matcher = pattern.matcher(body)
            if (matcher.find()) {
                try {
                    // Logic to map groups depends on the pattern index, but for simplicity we try to infer
                    // This is a simplified version. In production, we'd map specific patterns to specific group indices.
                    
                    // Let's handle specific patterns by index for better accuracy
                    if (pattern.pattern().startsWith("Purchase of")) {
                        return ParsedTransaction(
                            currency = matcher.group(1) ?: "AED",
                            amount = matcher.group(2)?.toDoubleOrNull() ?: 0.0,
                            merchantName = matcher.group(3)?.trim() ?: "Unknown",
                            cardIdentifier = matcher.group(4)?.trim()
                        )
                    } else if (pattern.pattern().startsWith("([A-Z]{3})")) {
                         return ParsedTransaction(
                            currency = matcher.group(1) ?: "AED",
                            amount = matcher.group(2)?.toDoubleOrNull() ?: 0.0,
                            cardIdentifier = matcher.group(3)?.trim(),
                            merchantName = matcher.group(4)?.trim() ?: "Unknown"
                        )
                    } else if (pattern.pattern().startsWith("Payment of")) {
                         return ParsedTransaction(
                            amount = matcher.group(1)?.toDoubleOrNull() ?: 0.0,
                            currency = matcher.group(2) ?: "AED",
                            merchantName = matcher.group(3)?.trim() ?: "Unknown",
                            cardIdentifier = null // Not always present
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}

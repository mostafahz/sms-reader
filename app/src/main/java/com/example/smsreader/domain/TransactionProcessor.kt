package com.example.smsreader.domain

import com.example.smsreader.data.database.TransactionEntity
import com.example.smsreader.data.database.WalletEntity
import com.example.smsreader.data.repository.SmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionProcessor(
    private val repository: SmsRepository,
    private val parser: TransactionParser = TransactionParser(),
    private val classifier: CategoryClassifier = CategoryClassifier()
) {

    suspend fun processSmsList(messages: List<SmsMessage>) = withContext(Dispatchers.IO) {
        val categories = repository.getAllCategoriesOneShot()
        
        // If no categories exist, we might need to seed them. 
        // For now, assume they are seeded or we handle empty case.

        for (msg in messages) {
            val parsed = parser.parse(msg.body)
            if (parsed != null) {
                // 1. Resolve Wallet
                var walletId = 0
                if (parsed.cardIdentifier != null) {
                    var wallet = repository.getWalletByIdentifier(parsed.cardIdentifier)
                    if (wallet == null) {
                        // Create new wallet
                        val newWallet = WalletEntity(
                            originalIdentifier = parsed.cardIdentifier,
                            userDisplayName = "Card ${parsed.cardIdentifier}",
                            colorHex = "#CCCCCC"
                        )
                        walletId = repository.insertWallet(newWallet).toInt()
                    } else {
                        walletId = wallet.id
                    }
                }

                // 2. Resolve Category
                val categoryId = classifier.classify(parsed.merchantName, categories)

                // 3. Save Transaction
                val transaction = TransactionEntity(
                    originalSmsBody = msg.body,
                    senderId = msg.address,
                    amount = parsed.amount,
                    currency = parsed.currency,
                    merchantName = parsed.merchantName,
                    categoryId = categoryId,
                    walletId = walletId,
                    timestamp = msg.date
                )
                repository.insertTransaction(transaction)
            }
        }
    }
}

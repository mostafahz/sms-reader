package com.example.smsreader.data.repository

import com.example.smsreader.data.database.CategoryEntity
import com.example.smsreader.data.database.SmsDao
import com.example.smsreader.data.database.TransactionEntity
import com.example.smsreader.data.database.WalletEntity
import kotlinx.coroutines.flow.Flow

class SmsRepository(private val smsDao: SmsDao) {
    // Transactions
    val allTransactions: Flow<List<TransactionEntity>> = smsDao.getAllTransactions()
    
    suspend fun insertTransaction(transaction: TransactionEntity) = smsDao.insertTransaction(transaction)
    
    suspend fun clearAllTransactions() = smsDao.clearAllTransactions()

    // Wallets
    val allWallets: Flow<List<WalletEntity>> = smsDao.getAllWallets()
    
    suspend fun getWalletByIdentifier(identifier: String): WalletEntity? = smsDao.getWalletByIdentifier(identifier)
    
    suspend fun insertWallet(wallet: WalletEntity): Long = smsDao.insertWallet(wallet)
    
    suspend fun updateWallet(wallet: WalletEntity) = smsDao.updateWallet(wallet)
    
    suspend fun clearAllWallets() = smsDao.clearAllWallets()

    // Categories
    val allCategories: Flow<List<CategoryEntity>> = smsDao.getAllCategories()
    
    suspend fun getAllCategoriesOneShot(): List<CategoryEntity> = smsDao.getAllCategoriesOneShot()
    
    suspend fun insertCategory(category: CategoryEntity) = smsDao.insertCategory(category)
    
    suspend fun clearAllCategories() = smsDao.clearAllCategories()
}

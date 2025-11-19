package com.example.smsreader.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {
    // Transactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    // Wallets
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Query("SELECT * FROM wallets WHERE originalIdentifier = :identifier LIMIT 1")
    suspend fun getWalletByIdentifier(identifier: String): WalletEntity?

    @Query("SELECT * FROM wallets")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Update
    suspend fun updateWallet(wallet: WalletEntity)
    
    @Query("DELETE FROM wallets")
    suspend fun clearAllWallets()

    // Categories
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesOneShot(): List<CategoryEntity>
    
    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()
}

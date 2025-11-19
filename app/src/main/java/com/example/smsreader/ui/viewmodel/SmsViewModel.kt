package com.example.smsreader.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smsreader.SmsReaderApp
import com.example.smsreader.data.database.CategoryEntity
import com.example.smsreader.data.database.TransactionEntity
import com.example.smsreader.data.database.WalletEntity
import com.example.smsreader.domain.SmsReader
import com.example.smsreader.domain.TransactionProcessor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SmsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as SmsReaderApp).container.smsRepository
    private val smsReader = SmsReader(application)
    private val processor = TransactionProcessor(repository)

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallets: StateFlow<List<WalletEntity>> = repository.allWallets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        seedCategories()
    }

    private fun seedCategories() {
        viewModelScope.launch {
            if (repository.getAllCategoriesOneShot().isEmpty()) {
                val defaults = listOf(
                    CategoryEntity(name = "Food", iconName = "food", keywordsJson = "[]"),
                    CategoryEntity(name = "Transportation", iconName = "transport", keywordsJson = "[]"),
                    CategoryEntity(name = "Groceries", iconName = "grocery", keywordsJson = "[]"),
                    CategoryEntity(name = "Health", iconName = "health", keywordsJson = "[]"),
                    CategoryEntity(name = "Bills", iconName = "bills", keywordsJson = "[]"),
                    CategoryEntity(name = "Shopping", iconName = "shopping", keywordsJson = "[]"),
                    CategoryEntity(name = "Other", iconName = "other", keywordsJson = "[]")
                )
                defaults.forEach { repository.insertCategory(it) }
            }
        }
    }

    fun scanSms(limit: Int, senderId: String?) {
        viewModelScope.launch {
            val messages = smsReader.readSms(limit, senderId)
            processor.processSmsList(messages)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllTransactions()
            repository.clearAllWallets()
            // We keep categories to avoid re-seeding constantly, or we can clear them too.
            // Let's keep them for now.
        }
    }

    fun updateWalletName(wallet: WalletEntity, newName: String) {
        viewModelScope.launch {
            repository.updateWallet(wallet.copy(userDisplayName = newName))
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SmsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SmsViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

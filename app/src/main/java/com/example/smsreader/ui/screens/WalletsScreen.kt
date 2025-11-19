package com.example.smsreader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smsreader.data.database.WalletEntity
import com.example.smsreader.ui.viewmodel.SmsViewModel

@Composable
fun WalletsScreen(viewModel: SmsViewModel) {
    val wallets by viewModel.wallets.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Wallets", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(wallets) { wallet ->
                WalletItem(wallet, onRename = { newName ->
                    viewModel.updateWalletName(wallet, newName)
                })
            }
        }
    }
}

@Composable
fun WalletItem(wallet: WalletEntity, onRename: (String) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(wallet.userDisplayName) }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Wallet Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { isEditing = false }) { Text("Cancel") }
                    TextButton(onClick = { 
                        onRename(editedName)
                        isEditing = false 
                    }) { Text("Save") }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(wallet.userDisplayName, style = MaterialTheme.typography.titleMedium)
                        Text(wallet.originalIdentifier, style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = { isEditing = true }) {
                        Text("Rename")
                    }
                }
            }
        }
    }
}

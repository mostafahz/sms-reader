package com.example.smsreader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smsreader.ui.viewmodel.SmsViewModel

@Composable
fun SettingsScreen(viewModel: SmsViewModel) {
    var senderId by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("10") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings & Debug", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = senderId,
            onValueChange = { senderId = it },
            label = { Text("Filter by Sender ID (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = limit,
            onValueChange = { limit = it },
            label = { Text("Max Messages to Scan") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { 
                val limitInt = limit.toIntOrNull() ?: 10
                val sender = if (senderId.isBlank()) null else senderId
                viewModel.scanSms(limitInt, sender)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan Now")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { viewModel.clearAllData() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Clear All Data")
        }
    }
}

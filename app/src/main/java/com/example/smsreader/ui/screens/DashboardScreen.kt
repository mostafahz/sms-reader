package com.example.smsreader.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.smsreader.data.database.CategoryEntity
import com.example.smsreader.data.database.TransactionEntity
import com.example.smsreader.ui.viewmodel.SmsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: SmsViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Spending Dashboard", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Simple Pie Chart Placeholder
        SpendingPieChart(transactions, categories)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Recent Transactions", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(transactions) { transaction ->
                TransactionItem(transaction, categories)
            }
        }
    }
}

@Composable
fun SpendingPieChart(transactions: List<TransactionEntity>, categories: List<CategoryEntity>) {
    // Calculate totals per category
    val totals = transactions.groupBy { it.categoryId }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
    
    val totalAmount = totals.values.sum()
    
    if (totalAmount == 0.0) {
        Text("No data to display", style = MaterialTheme.typography.bodyMedium)
        return
    }

    // Draw simple pie chart
    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = -90f
            val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan, Color.Gray)
            
            totals.entries.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value / totalAmount * 360).toFloat()
                val color = colors.getOrElse(index) { Color.Black }
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity, categories: List<CategoryEntity>) {
    val category = categories.find { it.id == transaction.categoryId }
    
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(transaction.merchantName, style = MaterialTheme.typography.bodyLarge)
                Text(category?.name ?: "Unknown", style = MaterialTheme.typography.bodySmall)
                Text(SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date(transaction.timestamp)), style = MaterialTheme.typography.labelSmall)
            }
            Text("${transaction.currency} ${transaction.amount}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

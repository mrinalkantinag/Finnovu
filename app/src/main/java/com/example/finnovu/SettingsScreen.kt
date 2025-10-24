package com.example.finnovu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finnovu.data.PaymentType
import com.example.finnovu.data.ExpenseCategory
import com.example.finnovu.ui.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val paymentTypes by viewModel.paymentTypes.collectAsState()
    val categories by viewModel.expenseCategories.collectAsState()
    var showAddPayment by remember { mutableStateOf(false) }
    var showAddCategory by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }, floatingActionButton = {}) { inner ->
        Column(modifier = Modifier.padding(inner).padding(16.dp)) {
            Text("Configure payment types", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(paymentTypes) { pt ->
                    ListItem(text = { Text(pt.name) }, modifier = Modifier.clickable { viewModel.onEditPaymentType(pt) }) {
                        // actions
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { showAddPayment = true }) { Text("Add payment type") }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Configure expense categories", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(categories) { cat ->
                    ListItem(text = { Text(cat.name) }, modifier = Modifier.clickable { viewModel.onEditExpenseCategory(cat) })
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { showAddCategory = true }) { Text("Add expense category") }
        }

        if (showAddPayment) {
            EditEntryDialog(title = "Add payment type", onDismiss = { showAddPayment = false }) { value ->
                viewModel.addPaymentType(value); showAddPayment = false
            }
        }
        if (showAddCategory) {
            EditEntryDialog(title = "Add expense category", onDismiss = { showAddCategory = false }) { value ->
                viewModel.addExpenseCategory(value); showAddCategory = false
            }
        }
    }
}

@Composable
fun EditEntryDialog(title: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onSave(text) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(title) },
        text = {
            OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
        }
    )
}
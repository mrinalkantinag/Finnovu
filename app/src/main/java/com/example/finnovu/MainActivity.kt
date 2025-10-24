package com.example.finnovu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finnovu.ui.theme.FinnovuTheme
import java.time.Month
import java.time.YearMonth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinnovuTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FloatingActionButton(onClick = { /* TODO: Handle add transaction */ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add transaction")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Part 1: Current Month to date Income and Expenses
            MonthSummary()

            Spacer(modifier = Modifier.height(16.dp))

            // Part 2: List of latest 5 transactions
            LatestTransactions()
        }
    }
}

@Composable
fun MonthSummary() {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var showMonthPicker by remember { mutableStateOf(false) }

    if (showMonthPicker) {
        MonthPickerDialog(
            onDismissRequest = { showMonthPicker = false },
            onMonthSelected = {
                selectedMonth = it
                showMonthPicker = false
            },
            initialMonth = selectedMonth
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${selectedMonth.month.name} ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { showMonthPicker = true }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select month")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Income: $1000")
                Text(text = "Expenses: $500")
            }
        }
    }
}

@Composable
fun MonthPickerDialog(
    onDismissRequest: () -> Unit,
    onMonthSelected: (YearMonth) -> Unit,
    initialMonth: YearMonth
) {
    val currentYear = YearMonth.now().year
    var selectedYear by remember { mutableStateOf(initialMonth.year) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedYear-- }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Year")
                }
                Text(text = selectedYear.toString())
                IconButton(onClick = { if (selectedYear < currentYear) selectedYear++ }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Year")
                }
            }
        },
        text = {
            Column {
                val months = Month.values()
                months.forEach { month ->
                    val yearMonth = YearMonth.of(selectedYear, month)
                    val isFutureMonth = yearMonth.isAfter(YearMonth.now())
                    TextButton(
                        onClick = { onMonthSelected(yearMonth) },
                        enabled = !isFutureMonth,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = month.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

data class Transaction(val id: Int, val description: String, val amount: Double, val type: String)

@Composable
fun LatestTransactions() {
    val transactions = listOf(
        Transaction(1, "Salary", 2000.0, "Income"),
        Transaction(2, "Groceries", -50.0, "Expense"),
        Transaction(3, "Rent", -800.0, "Expense"),
        Transaction(4, "Freelance", 300.0, "Income"),
        Transaction(5, "Dinner", -30.0, "Expense")
    )

    Column {
        Text(text = "Latest Transactions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = transaction.description)
            Text(text = "$${transaction.amount}", color = if (transaction.type == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FinnovuTheme {
        HomeScreen()
    }
}

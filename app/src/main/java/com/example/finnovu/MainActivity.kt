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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconToggleButton
import kotlinx.coroutines.launch
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavHostController
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                BottomIconsBar(navController = navController)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // Use NavHost for routing but animate the content changes
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                // Home content wrapped in Crossfade for a version-safe fade transition
                Crossfade(targetState = "home", animationSpec = tween(300)) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)) {
                        MonthSummary()
                        Spacer(modifier = Modifier.height(16.dp))
                        LatestTransactions()
                    }
                }
            }
            composable("settings") {
                Crossfade(targetState = "settings", animationSpec = tween(300)) {
                    SettingsScreen(onBack = { navController.popBackStack() }, snackbarHostState = snackbarHostState)
                }
            }
        }
    }
}

@Composable
fun BottomIconsBar(navController: NavHostController) {
    // Define the keys and icons
    val icons = listOf("add", "settings")
    val iconVectors = mapOf(
        "add" to Icons.Filled.Add,
        "settings" to Icons.Filled.Settings
    )
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // fixed item width to make centering math simpler
    val itemWidth = 72.dp
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val sidePadding = ((screenWidthDp - itemWidth) / 2).coerceAtLeast(0.dp)

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = sidePadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(icons) { index, key ->
            val isSelected = index == selectedIndex
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.12f else 1f, animationSpec = tween(300))

            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .animateItemPlacement(animationSpec = tween(350))
                    .wrapContentSize(Alignment.Center)
            ) {
                if (key == "add") {
                    // Use a circle background to mimic FAB
                    androidx.compose.material3.Surface(
                        shape = CircleShape,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .size(56.dp)
                            .scale(scale)
                    ) {
                        IconButton(onClick = {
                            selectedIndex = index
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            // placeholder for add transaction
                            coroutineScope.launch {
                                // animate scroll to center this item
                                listState.animateScrollToItem(index)
                            }
                        }) {
                            Icon(iconVectors[key]!!, contentDescription = "Add transaction")
                        }
                    }
                } else {
                    IconButton(onClick = {
                        selectedIndex = index
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                            // navigate after scrolling to give a smooth feeling
                            navController.navigate("settings")
                        }
                    }, modifier = Modifier.scale(scale)) {
                        Icon(iconVectors[key]!!, contentDescription = "${key.capitalize()}")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val paymentTypesFlow = SettingsDataStore.paymentTypesFlow(context)
    val expenseCategoriesFlow = SettingsDataStore.expenseCategoriesFlow(context)

    val paymentTypes by paymentTypesFlow.collectAsState(initial = listOf("Cash", "Card"))
    val expenseCategories by expenseCategoriesFlow.collectAsState(initial = listOf("Food", "Rent", "Transport"))

    var showDialogFor by remember { mutableStateOf<Pair<String, Int?>>(Pair("", null)) }
    var dialogText by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Settings", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Payment Types
        Text("Configure payment types", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(paymentTypes) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .semantics { contentDescription = "Payment type: $item" },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item)
                    Row {
                        IconButton(onClick = {
                            dialogText = item
                            showDialogFor = Pair("payment", index)
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteConfirm = Pair("payment", index) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        TextButton(onClick = {
            dialogText = ""
            showDialogFor = Pair("payment", null)
        }) { Text("Add payment type") }

        Spacer(modifier = Modifier.height(16.dp))

        // Expense Categories
        Text("Configure expense categories", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(expenseCategories) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .semantics { contentDescription = "Expense category: $item" },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item)
                    Row {
                        IconButton(onClick = {
                            dialogText = item
                            showDialogFor = Pair("category", index)
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteConfirm = Pair("category", index) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        TextButton(onClick = {
            dialogText = ""
            showDialogFor = Pair("category", null)
        }) { Text("Add expense category") }
    }

    var showDeleteConfirm by remember { mutableStateOf<Pair<String, Int?>>(Pair("", null)) }

    // Dialog for add/edit
    if (showDialogFor.first.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showDialogFor = Pair("", null) },
            title = {
                Text(if (showDialogFor.first == "payment") "Payment Type" else "Expense Category")
            },
            text = {
                OutlinedTextField(
                    value = dialogText,
                    onValueChange = { value ->
                        dialogText = value
                        // validation: non-empty and not duplicate
                        val candidate = value.trim()
                        dialogError = when {
                            candidate.isEmpty() -> "Name cannot be empty"
                            showDialogFor.first == "payment" && run {
                                val list = paymentTypes
                                val idx = showDialogFor.second
                                list.any { it.equals(candidate, ignoreCase = true) && (idx == null || list.getOrNull(idx) != it) }
                            } -> "This payment type already exists"
                            showDialogFor.first == "category" && run {
                                val list = expenseCategories
                                val idx = showDialogFor.second
                                list.any { it.equals(candidate, ignoreCase = true) && (idx == null || list.getOrNull(idx) != it) }
                            } -> "This category already exists"
                            else -> ""
                        }
                    },
                    label = { Text("Name") },
                    isError = dialogError.isNotEmpty()
                )
                if (dialogError.isNotEmpty()) {
                    Text(text = dialogError, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val kind = showDialogFor.first
                    val idx = showDialogFor.second
                    val candidate = dialogText.trim()
                    coroutineScope.launch {
                        if (kind == "payment") {
                            if (idx == null) SettingsDataStore.addPaymentType(context, candidate) else SettingsDataStore.updatePaymentType(context, idx, candidate)
                            // show snackbar after save
                            snackbarHostState.showSnackbar("Saved: $candidate")
                        } else if (kind == "category") {
                            if (idx == null) SettingsDataStore.addExpenseCategory(context, candidate) else SettingsDataStore.updateExpenseCategory(context, idx, candidate)
                            snackbarHostState.showSnackbar("Saved: $candidate")
                        }
                    }
                    showDialogFor = Pair("", null)
                    dialogText = ""
                    dialogError = ""
                }, enabled = dialogError.isEmpty()) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialogFor = Pair("", null) }) { Text("Cancel") }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirm.first.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = Pair("", null) },
            title = { Text("Confirm delete") },
            text = { Text("Are you sure you want to delete this ${if (showDeleteConfirm.first == "payment") "payment type" else "category"}? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    val kind = showDeleteConfirm.first
                    val idx = showDeleteConfirm.second
                    coroutineScope.launch {
                        if (kind == "payment" && idx != null) {
                            SettingsDataStore.removePaymentType(context, idx)
                            snackbarHostState.showSnackbar("Deleted payment type")
                        } else if (kind == "category" && idx != null) {
                            SettingsDataStore.removeExpenseCategory(context, idx)
                            snackbarHostState.showSnackbar("Deleted category")
                        }
                    }
                    showDeleteConfirm = Pair("", null)
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = Pair("", null) }) { Text("Cancel") }
            }
        )
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

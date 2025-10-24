package com.example.finnovu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finnovu.ui.theme.FinnovuTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinnovuTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController = navController) }
                    composable("settings") { SettingsScreen() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val icons = listOf(Icons.Filled.Home to "Home", Icons.Filled.Add to "Add", Icons.Filled.Settings to "Settings")
    val lazyState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Box(modifier = Modifier.fillMaxWidth().height(72.dp), contentAlignment = Alignment.Center) {
                    LazyRow(
                        state = lazyState,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        itemsIndexed(icons) { index, pair ->
                            val scale by animateFloatAsState(if (index == selectedIndex) 1.2f else 1f)
                            Icon(
                                imageVector = pair.first,
                                contentDescription = pair.second,
                                modifier = Modifier
                                    .padding(horizontal = 18.dp)
                                    .size((40 * scale).dp)
                                    .clickable {
                                        selectedIndex = index
                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.Medium)
                                        // center the tapped item
                                        scope.launch {
                                            lazyState.animateScrollToItem(index)
                                        }
                                        if (pair.second == "Settings") {
                                            navController.navigate("settings")
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)) {
            MonthSummary()
            Spacer(modifier = Modifier.height(16.dp))
            LatestTransactions()
        }
    }
}
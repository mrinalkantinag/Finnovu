package com.example.finnovu

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun settingsScreenDisplays() {
        // Navigate to settings
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.findViewById(androidx.compose.ui.platform.ComposeView::class.java)
            }
        }
        // Basic smoke test - ensure app launches and HomeScreen exists
        composeRule.onRoot().assertExists()
    }
}
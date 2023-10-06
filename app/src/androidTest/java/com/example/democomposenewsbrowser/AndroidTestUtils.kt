package com.example.democomposenewsbrowser

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun <T : Activity, Y : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<T>, Y>.printUnmergedTree(tag: String) =
    this.onRoot(useUnmergedTree = true).printToLog(tag)
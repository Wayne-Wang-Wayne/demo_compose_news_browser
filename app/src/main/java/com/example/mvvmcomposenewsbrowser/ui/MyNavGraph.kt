package com.example.mvvmcomposenewsbrowser.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mvvmcomposenewsbrowser.ui.util.*
import kotlinx.coroutines.CoroutineScope

@Composable
fun MyNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = MyDestinations.NEWS_ROUTE,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navAction: MyNavigationActions = remember(navController) {
        MyNavigationActions(navController)
    },
    context: Context = LocalContext.current
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry?.destination?.route?.let { currentRoute ->
            Log.d(MY_NAV_GRAPH_TAG, "Current Route: $currentRoute")
        }
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            MyDestinations.NEWS_ROUTE,
        ) {
            // TODO: call News page function
        }

        composable(
            MyDestinations.NEWS_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(MyDestinationsArgs.NEWS_LINK_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val newsLink = entry.arguments?.getString(MyDestinationsArgs.NEWS_LINK_ARG) ?: ""
            // TODO: call News Detail page function
        }
    }
}
const val MY_NAV_GRAPH_TAG = "MyNavGraph"
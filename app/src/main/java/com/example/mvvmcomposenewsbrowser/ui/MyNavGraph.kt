package com.example.mvvmcomposenewsbrowser.ui

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope

@Composable
fun MyNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = MyDestinations.NEWS_ROUTE,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navAction : MyNavigationActions = remember(navController) {
        MyNavigationActions(navController)
    }
) {

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
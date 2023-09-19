package com.example.mvvmcomposenewsbrowser.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.mvvmcomposenewsbrowser.ui.MyDestinations.NEWS_ROUTE
import com.example.mvvmcomposenewsbrowser.ui.MyDestinationsArgs.NEWS_LINK_ARG
import com.example.mvvmcomposenewsbrowser.ui.MyScreens.NEWS_DETAIL_SCREEN
import com.example.mvvmcomposenewsbrowser.ui.MyScreens.NEWS_SCREEN

private object MyScreens {
    const val NEWS_SCREEN = "news"
    const val NEWS_DETAIL_SCREEN = "newsDetail"
}

object MyDestinationsArgs {
    const val NEWS_LINK_ARG = "newsLink"
}

object MyDestinations {
    const val NEWS_ROUTE = NEWS_SCREEN
    const val NEWS_DETAIL_ROUTE = "$NEWS_DETAIL_SCREEN/{$NEWS_LINK_ARG}"
}

class MyNavigationActions(private val navController: NavHostController) {

    fun navigateToNews() {
        navController.navigate(NEWS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToNewsDetail(newsLink: String) {
        navController.navigate("$NEWS_DETAIL_SCREEN/$newsLink")
    }

    fun popBack() {
        navController.popBackStack()
    }

}
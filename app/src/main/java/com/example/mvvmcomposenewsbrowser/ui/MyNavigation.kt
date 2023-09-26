package com.example.mvvmcomposenewsbrowser.ui

import androidx.navigation.NavHostController
import com.example.mvvmcomposenewsbrowser.ui.NewsNav.NEWS_DETAIL_SCREEN_ROUTE

object NewsNav {
    const val NEWS_ROUTE = "news"
    const val NEWS_LIST_SCREEN_ROUTE = "news_list"
    const val NEWS_DETAIL_SCREEN_ROUTE = "news_detail"
}

class MyNavigationActions(private val navController: NavHostController) {

    fun navigateToNewsDetail() {
        navController.navigate(NEWS_DETAIL_SCREEN_ROUTE)
    }

    fun popBack() {
        navController.popBackStack()
    }

}
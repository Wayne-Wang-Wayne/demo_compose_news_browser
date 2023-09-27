package com.example.mvvmcomposenewsbrowser.ui

import androidx.navigation.NavHostController
import com.example.mvvmcomposenewsbrowser.ui.NewsNav.LIKED_NEWS_DETAIL_SCREEN_ROUTE
import com.example.mvvmcomposenewsbrowser.ui.NewsNav.LIKED_NEWS_LIST_SCREEN_ROUTE
import com.example.mvvmcomposenewsbrowser.ui.NewsNav.NEWS_DETAIL_SCREEN_ROUTE
import com.example.mvvmcomposenewsbrowser.ui.NewsNav.NEWS_LIST_SCREEN_ROUTE

object NewsNav {
    const val NEWS_ROUTE = "news"
    const val NEWS_LIST_SCREEN_ROUTE = "news_list"
    const val NEWS_DETAIL_SCREEN_ROUTE = "news_detail"
    const val LIKED_NEWS_LIST_SCREEN_ROUTE = "liked_news_list"
    const val LIKED_NEWS_DETAIL_SCREEN_ROUTE = "liked_news_detail"
}

class MyNavigationActions(private val navController: NavHostController) {

    fun navigateToNewsList() {
        navController.navigate(NEWS_LIST_SCREEN_ROUTE) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    fun navigateToLikedNewsList() {
        navController.navigate(LIKED_NEWS_LIST_SCREEN_ROUTE) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    fun popAllAndNavigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    fun navigateToNewsDetail() {
        navController.navigate(NEWS_DETAIL_SCREEN_ROUTE)
    }

    fun navigateToLikedNewsDetail() {
        navController.navigate(LIKED_NEWS_DETAIL_SCREEN_ROUTE)
    }

    fun popBack() {
        navController.popBackStack()
    }

}
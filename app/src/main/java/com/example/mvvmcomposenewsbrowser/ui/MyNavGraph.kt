package com.example.mvvmcomposenewsbrowser.ui

import android.content.Context
import android.util.Log
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mvvmcomposenewsbrowser.ui.news.NewsListScreen
import com.example.mvvmcomposenewsbrowser.ui.news.NewsViewModel
import com.example.mvvmcomposenewsbrowser.ui.news.NewsDetailScreen
import com.example.mvvmcomposenewsbrowser.ui.util.AppDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MyNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = NewsNav.NEWS_ROUTE,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navAction: MyNavigationActions = remember(navController) {
        MyNavigationActions(navController)
    },
    context: Context = LocalContext.current
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination
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
        newsGraph(
            navAction = navAction,
            navController = navController,
            drawerState = drawerState,
            coroutineScope = coroutineScope,
            currentRoute = currentRoute
        )
    }
}

fun NavGraphBuilder.newsGraph(
    navAction: MyNavigationActions,
    navController: NavHostController,
    drawerState: DrawerState,
    currentRoute: String,
    coroutineScope: CoroutineScope
) {
    navigation(startDestination = NewsNav.NEWS_LIST_SCREEN_ROUTE, route = NewsNav.NEWS_ROUTE) {
        composable(NewsNav.NEWS_LIST_SCREEN_ROUTE){
            val newsViewModel = hiltViewModel<NewsViewModel>()
            AppDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute
            ) {
                NewsListScreen(
                    onTopLeftIconPress = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    onNewsSelect = { parsedArticle ->
                        newsViewModel.updateTargetNews(parsedArticle)
                        navAction.navigateToNewsDetail()
                    },
                    viewModel = newsViewModel
                )
            }
        }
        composable(NewsNav.NEWS_DETAIL_SCREEN_ROUTE){
            val backStackEntry = remember {
                navController.getBackStackEntry(NewsNav.NEWS_LIST_SCREEN_ROUTE)
            }
            val newsViewModel = hiltViewModel<NewsViewModel>(backStackEntry)
            NewsDetailScreen(
                onTopLeftIconPress = {
                    navAction.popBack()
                },
                viewModel = newsViewModel
            )
        }
    }
}
const val MY_NAV_GRAPH_TAG = "MyNavGraph"
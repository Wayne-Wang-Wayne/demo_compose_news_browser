package com.example.democomposenewsbrowser.ui

import android.content.Context
import android.util.Log
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.democomposenewsbrowser.ui.news.*
import com.example.democomposenewsbrowser.ui.util.AppDrawer
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
        composable(NewsNav.NEWS_LIST_SCREEN_ROUTE) {
            val newsViewModel = hiltViewModel<NewsViewModel>()
            AppDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                onDrawerChoose = { route ->
                    navAction.popAllAndNavigateTo(route)
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
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
        composable(NewsNav.NEWS_DETAIL_SCREEN_ROUTE) {
            val backStackEntry = remember(it) {
                navController.getBackStackEntry(NewsNav.NEWS_LIST_SCREEN_ROUTE)
            }
            val newsViewModel = hiltViewModel<NewsViewModel>(backStackEntry)
            val newsUiState by newsViewModel.newsUiState.collectAsStateWithLifecycle()
            val url by remember(newsUiState.targetNews) {
                derivedStateOf {
                    newsUiState.targetNews?.url
                }
            }
            val isLiked by remember(newsUiState.targetNews) {
                derivedStateOf {
                    newsUiState.targetNews?.isLiked ?: false
                }
            }
            NewsDetailScreen(
                onTopLeftIconPress = {
                    navAction.popBack()
                },
                url = url,
                isLiked = isLiked,
                onToggleLike = {
                    newsUiState.targetNews?.let { targetNews ->
                        newsViewModel.toggleLike(targetNews)
                    }
                }
            )
        }

        composable(NewsNav.LIKED_NEWS_LIST_SCREEN_ROUTE) {
            val likedNewsViewModel = hiltViewModel<LikedNewsViewModel>()
            AppDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                onDrawerChoose = { route ->
                    navAction.popAllAndNavigateTo(route)
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            ) {
                LikedNewsListScreen(
                    onTopLeftIconPress = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    onNewsSelect = {
                        likedNewsViewModel.targetNews(it)
                        navAction.navigateToLikedNewsDetail()
                    },
                    viewModel = likedNewsViewModel
                )
            }
        }

        composable(NewsNav.LIKED_NEWS_DETAIL_SCREEN_ROUTE) {
            val backStackEntry = remember(it) {
                navController.getBackStackEntry(NewsNav.LIKED_NEWS_LIST_SCREEN_ROUTE)
            }
            val likedNewsViewModel = hiltViewModel<LikedNewsViewModel>(backStackEntry)
            val likedUiState by likedNewsViewModel.likedUiState.collectAsStateWithLifecycle()
            val url by remember(likedUiState.targetNews) {
                derivedStateOf {
                    likedUiState.targetNews?.url
                }
            }
            val isLiked by remember(likedUiState.targetNews) {
                derivedStateOf {
                    likedUiState.targetNews?.isLiked ?: false
                }
            }
            NewsDetailScreen(
                onTopLeftIconPress = {
                    navAction.popBack()
                },
                url = url,
                isLiked = isLiked,
                onToggleLike = {
                    likedUiState.targetNews?.let { targetNews ->
                        likedNewsViewModel.toggleDetailLike(targetNews)
                    }
                }
            )
        }
    }
}
const val MY_NAV_GRAPH_TAG = "MyNavGraph"
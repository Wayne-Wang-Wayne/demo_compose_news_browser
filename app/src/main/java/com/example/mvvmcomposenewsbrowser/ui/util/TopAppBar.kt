package com.example.mvvmcomposenewsbrowser.ui.util

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import com.example.mvvmcomposenewsbrowser.R
import com.example.mvvmcomposenewsbrowser.ui.MyDestinations
import com.example.mvvmcomposenewsbrowser.ui.MyNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    onIconPress: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title
            )
        },
        navigationIcon = {
            IconButton(onIconPress) {
                icon()
            }
        },
        modifier = modifier
    )
}


fun NavBackStackEntry?.getTitle(context: Context): String = when (this?.destination?.route) {
    MyDestinations.NEWS_ROUTE -> context.getString(R.string.news_title)
    MyDestinations.NEWS_DETAIL_ROUTE -> context.getString(R.string.news_detail_title)
    else -> ""
}

enum class TopAppBarType {
    Drawer, Back
}

fun NavBackStackEntry?.getTopAppBarType(): TopAppBarType = when (this?.destination?.route) {
    MyDestinations.NEWS_ROUTE -> TopAppBarType.Drawer
    MyDestinations.NEWS_DETAIL_ROUTE -> TopAppBarType.Back
    else -> TopAppBarType.Drawer
}

fun TopAppBarType.getOnClick(
    navAction : MyNavigationActions,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope
): () -> Unit = when(this) {
    TopAppBarType.Drawer -> {
        {
            if (drawerState.isOpen) {
                coroutineScope.launch {
                    drawerState.close()
                }
            } else {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
        }
    }
    TopAppBarType.Back -> {
        {
            navAction.popBack()
        }
    }
}

@Composable
fun DrawerIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        Icons.Default.Menu, contentDescription = "drawer",
        modifier = modifier
    )
}

@Composable
fun BackIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        Icons.Default.ArrowBack, contentDescription = "back press",
        modifier = modifier
    )
}
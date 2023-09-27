package com.example.mvvmcomposenewsbrowser.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.mvvmcomposenewsbrowser.R
import androidx.compose.material.DrawerState
import com.example.mvvmcomposenewsbrowser.ui.NewsNav


@Composable
fun AppDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    onDrawerChoose: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                DrawerContent(
                    currentRoute = currentRoute,
                    onDrawerChoose = onDrawerChoose
                )
            }
        }
    ) {
        content()
    }
}

@Composable
fun DrawerContent(
    currentRoute: String,
    onDrawerChoose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        DrawerHeader()
        DrawerBody(
            currentRoute = currentRoute,
            onDrawerChoose = onDrawerChoose
        )
    }
}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.drawer_header_height))
            .background(MaterialTheme.colorScheme.secondary)
    )
}

@Composable
fun DrawerBody(
    currentRoute: String,
    onDrawerChoose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        for (category in DrawerCategory.values()) {
            DrawerButton(
                isSelected = category.route == currentRoute,
                onDrawerChoose = {
                    onDrawerChoose(category.route)
                },
                name = category.titleName
            )
        }
    }
}

@Composable
fun DrawerButton(
    isSelected: Boolean,
    onDrawerChoose: () -> Unit,
    name: String,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }
    TextButton(
        onClick = onDrawerChoose,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Rounded.ShoppingCart,
                contentDescription = null,
                tint = tintColor
            )
            Spacer(
                modifier = Modifier.width(16.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(color = tintColor)
            )
        }
    }
}

//@Preview("Drawer contents", showBackground = true)
//@Composable
//fun DrawerContentPreview() {
//    MVVMComposeNewsBrowserTheme {
//        Surface {
//            DrawerContent()
//        }
//    }
//}

enum class DrawerCategory(
    val route: String,
    val titleName: String
    ) {
    NEWS(
        NewsNav.NEWS_LIST_SCREEN_ROUTE,
        "新聞列表"
    ),
    LIKED_NEWS(
        NewsNav.LIKED_NEWS_LIST_SCREEN_ROUTE,
        "收藏新聞"
    )
}
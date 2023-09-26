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


@Composable
fun AppDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentRoute = currentRoute
            )
        }
    ) {
        content()
    }
}

@Composable
fun DrawerContent(
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        DrawerHeader()
        DrawerBody()
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
    modifier: Modifier = Modifier
) {
    Column {
        DrawerButton(isSelected = true)
        DrawerButton(isSelected = false)
    }
}

@Composable
fun DrawerButton(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }
    TextButton(
        onClick = { /*TODO*/ },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier.width(5.dp)
            )
            Icon(
                Icons.Rounded.ShoppingCart,
                contentDescription = null,
                tint = tintColor
            )
            Spacer(
                modifier = Modifier.width(10.dp)
            )
            Text(
                text = "我是按鈕",
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

enum class Drawer
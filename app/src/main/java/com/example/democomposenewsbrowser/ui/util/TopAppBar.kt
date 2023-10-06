package com.example.democomposenewsbrowser.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.democomposenewsbrowser.R

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

@Composable
fun DrawerIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        Icons.Default.Menu, contentDescription = stringResource(R.string.open_drawer),
        modifier = modifier
    )
}

@Composable
fun BackIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_press),
        modifier = modifier
    )
}
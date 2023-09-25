package com.example.mvvmcomposenewsbrowser.ui.newsdetail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mvvmcomposenewsbrowser.R
import com.example.mvvmcomposenewsbrowser.ui.util.BackIcon
import com.example.mvvmcomposenewsbrowser.ui.util.MyTopAppBar
import com.example.mvvmcomposenewsbrowser.ui.util.WebViewWithLoading

@Composable
fun NewsDetailScreen(
    newsLink: String,
    onTopLeftIconPress: () -> Unit,
    viewModel: NewsDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val newsDetailUiState by viewModel.newsDetailUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = stringResource(R.string.news_detail),
                onIconPress = onTopLeftIconPress,
                icon = {
                    BackIcon()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
            ) {
                val scale by animateFloatAsState(
                    if (true) 1.2f else 1f,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMedium
                    )
                )
                val color by animateColorAsState(if (true) Color.Red else Color.Gray)

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .scale(scale)
                        .size(24.dp),
                    tint = color
                )
            }
        }
    ) {
        WebViewWithLoading(
            url = newsLink,
            modifier = modifier.padding(it)
        )
    }
}
package com.example.mvvmcomposenewsbrowser.ui.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HeartButton() {
    var isLiked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (isLiked) 1.2f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium
        )
    )
    val color by animateColorAsState(if (isLiked) Color.Red else Color.Gray,)

    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable {
                isLiked = !isLiked
            },
        contentAlignment = Alignment.Center
    ) {
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

@Composable
fun ShimmerLoadingBlock(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    Spacer(
        modifier = modifier
        .background(Color.LightGray.copy(alpha = alpha))
    )
}
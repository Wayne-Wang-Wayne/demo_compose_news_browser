package com.example.mvvmcomposenewsbrowser.ui.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.mvvmcomposenewsbrowser.R
import com.example.mvvmcomposenewsbrowser.ui.theme.MVVMComposeNewsBrowserTheme
import com.example.mvvmcomposenewsbrowser.ui.util.DrawerIcon
import com.example.mvvmcomposenewsbrowser.ui.util.HeartButton
import com.example.mvvmcomposenewsbrowser.ui.util.MyTopAppBar
import com.example.mvvmcomposenewsbrowser.ui.util.ShimmerLoadingBlock

@Composable
fun NewsScreen(
    onIconPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MyTopAppBar(
                title = stringResource(id = R.string.news_title),
                onIconPress = onIconPress,
                icon = {
                    DrawerIcon()
                }
            )
        }
    ) {
        NewsScreenBody(
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun NewsScreenBody(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            listOf(1, 2, 3, 4, 5),
            key = { it }
        ) {
            NewsListCard(
                modifier = Modifier.height(120.dp)
            )
        }
    }
}

@Composable
fun NewsListCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
    ) {
            Row(
                modifier = Modifier.padding(
                    vertical = 8.dp,
                    horizontal = 10.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://static.politico.com/9f/56/723290e644c4bbf14ae3a4873cb4/auto-workers-strike-55578.jpg")
                        .crossfade(true)
                        .build(),
                    loading = {
                        ShimmerLoadingBlock()
                    },
                    error = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_broken_image),
                            contentDescription = null
                        )
                    },
                    contentDescription = "YOASOBI公布亞巡有台灣！ 12月現身簡單生活節為個唱暖身 - 鏡週刊",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "YOASOBI公布亞巡有台灣！ 12月現身簡單生活節為個唱暖身 - 鏡週刊",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        HeartButton()
                        Text(
                            text = "2023-09-19T23:31:00Z",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    MVVMComposeNewsBrowserTheme {
        NewsScreen(
            onIconPress = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewsListCardPreview() {
    NewsListCard()
}
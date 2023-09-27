package com.example.mvvmcomposenewsbrowser.ui.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.mvvmcomposenewsbrowser.R
import com.example.mvvmcomposenewsbrowser.data.news.ParsedNews
import com.example.mvvmcomposenewsbrowser.ui.util.BasicAlertDialog
import com.example.mvvmcomposenewsbrowser.ui.util.DrawerIcon
import com.example.mvvmcomposenewsbrowser.ui.util.MyTopAppBar

@Composable
fun LikedNewsListScreen(
    onTopLeftIconPress: () -> Unit,
    onNewsSelect: (ParsedNews) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LikedNewsViewModel = hiltViewModel()
) {

    val likedUiState by viewModel.likedUiState.collectAsStateWithLifecycle()

    val isEmpty by remember(likedUiState) {
        derivedStateOf {
            likedUiState.likedNewsList.isEmpty()
        }
    }

    val likedNewsList by remember(likedUiState) {
        derivedStateOf {
            likedUiState.likedNewsList
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MyTopAppBar(
                title = stringResource(id = R.string.liked_news_title),
                onIconPress = onTopLeftIconPress,
                icon = {
                    DrawerIcon()
                }
            )
        }
    ) {
        if (isEmpty) {
            LikedEmptyBody()
        } else {
            LikedNewsBody(
                likedNewsList = likedNewsList,
                contentsListState = rememberLazyListState(),
                onNewsSelect = onNewsSelect,
                onDeleteConfirm = { dislikedNews ->
                    viewModel.dislikeNews(dislikedNews)
                },
                modifier = Modifier.padding(it)
            )
        }
    }
}

@Composable
fun LikedNewsBody(
    likedNewsList: List<ParsedNews>,
    contentsListState: LazyListState = rememberLazyListState(),
    onNewsSelect: (ParsedNews) -> Unit,
    onDeleteConfirm: (ParsedNews) -> Unit,
    modifier: Modifier = Modifier
) {
    var openAlertDialog by remember { mutableStateOf(false) }
    var deleteNews by remember { mutableStateOf(ParsedNews("", "", "", "", "", true)) }

    LazyColumn(
        state = contentsListState,
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(
            likedNewsList,
            key = { it.url }
        ) { parsedArticle ->
            LikedNewsListCard(
                author = parsedArticle.author,
                title = parsedArticle.title,
                onNewsSelect = {
                    onNewsSelect(parsedArticle)
                },
                publishedAt = parsedArticle.publishedAt,
                imgUrl = parsedArticle.imgUrl,
                onDeleteClick = {
                    deleteNews = parsedArticle
                    openAlertDialog = true
                }
            )
        }
    }
    if(openAlertDialog) {
        DeleteNewsDialog(
            onCancelClick = {
                openAlertDialog = false
            },
            onConfirmClick = {
                onDeleteConfirm(deleteNews)
                openAlertDialog = false
            }
        )
    }
}

@Composable
fun LikedNewsListCard(
    author: String,
    title: String,
    onNewsSelect: () -> Unit,
    publishedAt: String,
    imgUrl: String,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable {
                onNewsSelect()
            },
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
                    .data(imgUrl)
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
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = author,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = publishedAt,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Divider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxHeight()
                    .padding(start = 10.dp, end = 5.dp)
                    .width(2.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        onDeleteClick()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun LikedEmptyBody(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = stringResource(R.string.empty_liked_news))
    }
}

@Composable
fun DeleteNewsDialog(
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = {
            onCancelClick()
        },
        onConfirmation = {
            onConfirmClick()
        },
        dialogTitle = stringResource(R.string.dialog_delete_title),
        dialogText = stringResource(R.string.dialog_delete_body),
        icon = Icons.Outlined.Delete
    )
}
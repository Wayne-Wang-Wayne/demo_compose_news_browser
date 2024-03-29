package com.example.democomposenewsbrowser.ui.news

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.democomposenewsbrowser.R
import com.example.democomposenewsbrowser.data.news.ParsedNews
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsCategory
import com.example.democomposenewsbrowser.data.news.datasources.remote.toChinese
import com.example.democomposenewsbrowser.ui.theme.DemoComposeNewsBrowserTheme
import com.example.democomposenewsbrowser.ui.util.*

private const val NEWS_SCREEN_TAG = "NEWS_SCREEN_TAG"

@Composable
fun NewsListScreen(
    onTopLeftIconPress: () -> Unit,
    onNewsSelect: (ParsedNews) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val newsUiState by viewModel.newsUiState.collectAsStateWithLifecycle()

    val vMIsLoading by remember {
        derivedStateOf {
            newsUiState.isLoading
        }
    }
    val vMIsError by remember {
        derivedStateOf {
            newsUiState.isError
        }
    }
    val vMNewsList by remember {
        derivedStateOf {
            newsUiState.newsList
        }
    }
    val vMErrorMsg by remember {
        derivedStateOf {
            newsUiState.errorMsg
        }
    }
    val vMNewsCategory by remember {
        derivedStateOf {
            newsUiState.newsCategory
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MyTopAppBar(
                title = stringResource(id = R.string.news_list_title),
                onIconPress = onTopLeftIconPress,
                icon = {
                    DrawerIcon()
                }
            )
        }
    ) {
        NewsScreenBody(
            isLoading = vMIsLoading,
            isError = vMIsError,
            newsList = vMNewsList,
            errMsg = vMErrorMsg,
            selectedCategory = vMNewsCategory,
            onCategorySelect = viewModel::getFreshNews,
            onLikeClick = viewModel::toggleLike,
            onRetry = viewModel::getFreshNews,
            onNewsSelect = onNewsSelect,
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewsScreenBody(
    isLoading: Boolean,
    isError: Boolean,
    newsList: List<ParsedNews>,
    errMsg: String,
    selectedCategory: NewsCategory,
    onCategorySelect: (NewsCategory) -> Unit,
    onLikeClick: (ParsedNews) -> Unit,
    onRetry: (NewsCategory) -> Unit,
    onNewsSelect: (ParsedNews) -> Unit,
    modifier: Modifier = Modifier,
    contentsListState: LazyListState = rememberLazyListState(),
    pickerListState: LazyListState = rememberLazyListState()
) {
    Column(
        modifier = modifier
    ) {
        NewsCategoryPicker(
            selectedCategory = selectedCategory,
            pickerListState = pickerListState,
            onCategorySelect = onCategorySelect
        )
        if (isError) {
            LaunchedEffect(errMsg) {
                Log.d(NEWS_SCREEN_TAG, errMsg)
            }
            ErrorRetryLayout(
                onRetryClick = {
                    onRetry(selectedCategory)
                }
            )
        } else {
            val pullRefreshState = rememberPullRefreshState(isLoading, { onRetry(selectedCategory) })
            Box(Modifier.pullRefresh(pullRefreshState)) {
                NewsSuccessBody(
                    isLoading = isLoading,
                    newsList = newsList,
                    contentsListState = contentsListState,
                    onLikeClick = onLikeClick,
                    onNewsSelect = onNewsSelect
                )
                PullRefreshIndicator(isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun NewsSuccessBody(
    isLoading: Boolean,
    newsList: List<ParsedNews>,
    contentsListState: LazyListState,
    onNewsSelect: (ParsedNews) -> Unit,
    onLikeClick: (ParsedNews) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = contentsListState,
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        if(isLoading) {
            for(count in 1..8) {
                item(key = count) {
                    ShimmerNewsCard()
                }
            }
        } else {
            items(
                newsList,
                key = { it.url }
            ) { parsedArticle ->
                NewsListCard(
                    author = parsedArticle.author,
                    title = parsedArticle.title,
                    onNewsSelect = {
                        onNewsSelect(parsedArticle)
                    },
                    publishedAt = parsedArticle.publishedAt,
                    imgUrl = parsedArticle.imgUrl,
                    isLiked = parsedArticle.isLiked,
                    onLikeClick = {
                        onLikeClick(parsedArticle)
                    }
                )
            }
        }
    }
}

@Composable
fun NewsListCard(
    author: String,
    title: String,
    onNewsSelect: () -> Unit,
    publishedAt: String,
    imgUrl: String,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
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
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        HeartButton(
                            isLiked = isLiked,
                            onLikeClick = onLikeClick
                        )
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
            }
    }
}

@Composable
fun NewsCategoryPicker(
    selectedCategory: NewsCategory,
    onCategorySelect: (NewsCategory) -> Unit,
    pickerListState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyRow(
        state = pickerListState,
        modifier = modifier,
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(
            NewsCategory.values(),
            key = { it.tag }
        ) { newsCategory ->
            PickItem(
                newsCategory == selectedCategory,
                onCategorySelect = onCategorySelect,
                newsCategory = newsCategory
            )
        }
    }
}

@Composable
fun PickItem(
    isSelected: Boolean,
    onCategorySelect: (NewsCategory) -> Unit,
    newsCategory: NewsCategory,
    modifier: Modifier = Modifier
) {
    val color = if(isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
    Button(
        onClick = {
            if (!isSelected) {
                onCategorySelect(newsCategory)
            }
        },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
    ){
        Text(text = newsCategory.toChinese())
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    DemoComposeNewsBrowserTheme {
        NewsListScreen(
            onTopLeftIconPress = {},
            onNewsSelect = {}
        )
    }
}

/**
 * News Shimmer Composable
 * */

@Composable
fun ShimmerLoadingBlock(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .showShimmerBackground()
            .size(100.dp)
    )
}

@Composable
fun ShimmerNewsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerLoadingBlock()
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Column {
                    for (i in 1..2) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .showShimmerBackground()
                        )
                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HeartButton(
                        isLiked = false,
                        onLikeClick = {}
                    )
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Spacer(modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .showShimmerBackground()
                        )
                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )
                        Spacer(modifier = Modifier
                            .width(200.dp)
                            .height(15.dp)
                            .showShimmerBackground()
                        )
                    }
                }
            }
        }
    }
}
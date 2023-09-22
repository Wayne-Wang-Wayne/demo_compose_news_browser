package com.example.mvvmcomposenewsbrowser.ui.news

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.example.mvvmcomposenewsbrowser.R
import com.example.mvvmcomposenewsbrowser.data.news.ParsedArticle
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsCategory
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.toChinese
import com.example.mvvmcomposenewsbrowser.ui.theme.MVVMComposeNewsBrowserTheme
import com.example.mvvmcomposenewsbrowser.ui.util.*

@Composable
fun NewsScreen(
    onTopLeftIconPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val newsUiState by viewModel.newsUiState.collectAsStateWithLifecycle()
    val newCategory by rememberSaveable {
        mutableStateOf(NewsCategory.WHATEVER)
    }

    LaunchedEffect(newCategory) {
        viewModel.getFreshNews(newCategory)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MyTopAppBar(
                title = stringResource(id = R.string.news_title),
                onIconPress = onTopLeftIconPress,
                icon = {
                    DrawerIcon()
                }
            )
        }
    ) {
        NewsScreenBody(
            isLoading = newsUiState.isLoading,
            isError = newsUiState.isError,
            newsList = newsUiState.newsList,
            errMsg = newsUiState.errorMsg,
            selectedCategory = newsUiState.newsCategory,
            onCategorySelect = viewModel::getFreshNews,
            onLikeClick = viewModel::toggleLike,
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun NewsScreenBody(
    isLoading: Boolean,
    isError: Boolean,
    newsList: List<ParsedArticle>,
    errMsg: String,
    selectedCategory: NewsCategory,
    onCategorySelect: (NewsCategory) -> Unit,
    onLikeClick: (ParsedArticle) -> Unit,
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
            // TODO Call Error Body
        } else {
            NewsSuccessBody(
                isLoading = isLoading,
                newsList = newsList,
                contentsListState = contentsListState,
                onLikeClick = onLikeClick
            )
        }
    }
}

@Composable
fun NewsSuccessBody(
    isLoading: Boolean,
    newsList: List<ParsedArticle>,
    contentsListState: LazyListState,
    //    onArticleClick: (String) -> Unit,
    onLikeClick: (ParsedArticle) -> Unit,
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
                    url = parsedArticle.url,
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
    url: String,
    publishedAt: String,
    imgUrl: String,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
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
    MVVMComposeNewsBrowserTheme {
        NewsScreen(
            onTopLeftIconPress = {}
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
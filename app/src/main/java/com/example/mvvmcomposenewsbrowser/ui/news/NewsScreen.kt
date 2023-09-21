package com.example.mvvmcomposenewsbrowser.ui.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.mvvmcomposenewsbrowser.R
import com.example.mvvmcomposenewsbrowser.data.news.ParsedArticle
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsCategory
import com.example.mvvmcomposenewsbrowser.ui.theme.MVVMComposeNewsBrowserTheme
import com.example.mvvmcomposenewsbrowser.ui.util.DrawerIcon
import com.example.mvvmcomposenewsbrowser.ui.util.HeartButton
import com.example.mvvmcomposenewsbrowser.ui.util.MyTopAppBar
import com.example.mvvmcomposenewsbrowser.ui.util.ShimmerLoadingBlock

@Composable
fun NewsScreen(
    onTopLeftIconPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val newsUiState by viewModel.newsUiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.getFreshNews(NewsCategory.WHATEVER)
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
            newsUiState = newsUiState,
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun NewsScreenBody(
    newsUiState: NewsUiState,
    modifier: Modifier = Modifier,
    contentsListState: LazyListState = rememberLazyListState(),
    pickerListState: LazyListState = rememberLazyListState()
) {
    Column(
        modifier = modifier
    ) {
        NewsCategoryPicker(
            pickerListState = pickerListState
        )
        when(newsUiState) {
            is NewsUiState.Loading -> {

            }
            is NewsUiState.Success -> {
                NewsSuccessBody(
                    newsList = newsUiState.newsList,
                    contentsListState = contentsListState
                )
            }
            is NewsUiState.Error -> {

            }
        }
    }
}

@Composable
fun NewsSuccessBody(
    newsList: List<ParsedArticle>,
    contentsListState: LazyListState,
    modifier: Modifier = Modifier,
//    onArticleClick: (String) -> Unit,
//    onHeartClick: (String) -> Unit
) {
    LazyColumn(
        state = contentsListState,
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(
            newsList,
            key = { it.title }
        ) { parsedArticle ->
            NewsListCard(
                author = parsedArticle.author,
                title = parsedArticle.title,
                url = parsedArticle.url,
                publishedAt = parsedArticle.publishedAt,
                imgUrl = parsedArticle.imgUrl,
                isLiked = parsedArticle.isLiked
            )
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
                        HeartButton()
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
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
            key = { it }
        ) {
            PickItem(true)
        }
    }
}

@Composable
fun PickItem(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if(isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
    Button(
        onClick = { },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
    ){
        Text( text = "娛樂" )
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
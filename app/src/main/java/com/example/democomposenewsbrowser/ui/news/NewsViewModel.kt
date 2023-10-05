package com.example.democomposenewsbrowser.ui.news

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democomposenewsbrowser.data.news.NewsRepository
import com.example.democomposenewsbrowser.data.news.ParsedNews
import com.example.democomposenewsbrowser.data.news.ParsedNewsListData
import com.example.democomposenewsbrowser.data.news.Status
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsUiState(
    val newsCategory: NewsCategory = NewsCategory.WHATEVER,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = "",
    val newsList: SnapshotStateList<ParsedNews> = mutableStateListOf(),
    val targetNews: ParsedNews? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _newsUiState: MutableStateFlow<NewsUiState> = MutableStateFlow(NewsUiState(isLoading = true))
    val newsUiState = _newsUiState.asStateFlow()

    init {
        getFreshNews(NewsCategory.WHATEVER)
    }

    fun getFreshNews(newsCategory: NewsCategory) {
        _newsUiState.update {
            it.copy(
                newsCategory = newsCategory,
                isLoading = true
            )
        }
        if (newsCategory == NewsCategory.WHATEVER) {
            newsRepository.getWhateverNews().updateNewsUiState()
        } else {
            newsRepository.getSpecificNews(newsCategory.tag).updateNewsUiState()
        }
    }

    fun toggleLike(parsedNews: ParsedNews) = viewModelScope.launch {
        val newsList = _newsUiState.value.newsList
        val targetIndex = newsList.map { it.url }.indexOf(parsedNews.url)
        if(targetIndex != -1) {
            val currentArticle = newsList[targetIndex]
            val isLiked = currentArticle.isLiked
            if(isLiked) {
                newsRepository.dislikeNews(parsedNews)
            } else {
                newsRepository.likeNews(parsedNews)
            }
            newsList[targetIndex] = newsList[targetIndex].copy(isLiked = !isLiked)
            val targetNews = _newsUiState.value.targetNews
            if (currentArticle == targetNews) {
                // 連target都要更新
                _newsUiState.update {
                    it.copy(
                        targetNews = targetNews.copy(isLiked = !targetNews.isLiked)
                    )
                }
            }
        }
    }

    fun updateTargetNews(parsedNews: ParsedNews) {
        _newsUiState.update {
            it.copy(targetNews = parsedNews)
        }
    }

    private fun Flow<ParsedNewsListData>.updateNewsUiState() = viewModelScope.launch {
        this@updateNewsUiState.collect { parsedListData ->
            when (val currentStatus = parsedListData.status) {
                is Status.Success -> {
                    if (parsedListData.parsedNews.isNullOrEmpty()) {
                        _newsUiState.update {
                            it.copy(
                                isError = true,
                                isLoading = false,
                                errorMsg = "哇，目前沒有對應的新聞資料"
                            )
                        }
                    } else {
                        _newsUiState.update {
                            it.copy(
                                isError = false,
                                isLoading = false,
                                newsList = parsedListData.parsedNews.toMutableStateList()
                            )
                        }
                    }
                }
                is Status.ERROR -> {
                    _newsUiState.update {
                        it.copy(
                            isError = true,
                            isLoading = false,
                            errorMsg = currentStatus.msg
                        )
                    }
                }
            }
        }
    }
}


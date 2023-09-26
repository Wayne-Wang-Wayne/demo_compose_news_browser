package com.example.mvvmcomposenewsbrowser.ui.news

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmcomposenewsbrowser.data.news.NewsRepository
import com.example.mvvmcomposenewsbrowser.data.news.ParsedArticle
import com.example.mvvmcomposenewsbrowser.data.news.ParsedNewsListData
import com.example.mvvmcomposenewsbrowser.data.news.Status
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsUiState(
    val newsCategory: NewsCategory = NewsCategory.WHATEVER,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = "",
    val newsList: List<ParsedArticle> = listOf(),
    val targetNews: ParsedArticle? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _newsUiState: MutableStateFlow<NewsUiState> = MutableStateFlow(NewsUiState(isLoading = true))
    val newsUiState = _newsUiState.asStateFlow()

    fun getFreshNews(newsCategory: NewsCategory) {
        _newsUiState.update { it.copy(newsCategory = newsCategory) }
        if (newsCategory == NewsCategory.WHATEVER) {
            newsRepository.getWhateverNews().updateNewsUiState()
        } else {
            newsRepository.getSpecificNews(newsCategory.tag).updateNewsUiState()
        }
    }

    fun toggleLike(parsedArticle: ParsedArticle) = viewModelScope.launch {
        _newsUiState.update { newsUiState ->
            val targetIndex = newsUiState.newsList.indexOf(parsedArticle)
            if(targetIndex != -1) {
                val currentArticle = newsUiState.newsList[targetIndex]
                val isLiked = currentArticle.isLiked
                if(isLiked) {
                    newsRepository.dislikeArticle(parsedArticle)
                } else {
                    newsRepository.likeArticle(parsedArticle)
                }
                val updatedNewsList = newsUiState.newsList.toMutableList()
                updatedNewsList[targetIndex] = currentArticle.copy(isLiked = !isLiked)
                if (currentArticle == newsUiState.targetNews) {
                    // 連target都要更新
                    val target = newsUiState.targetNews
                    newsUiState.copy(
                        newsList = updatedNewsList,
                        targetNews = target.copy(isLiked = !target.isLiked)
                    )
                } else {
                    newsUiState.copy(
                        newsList = updatedNewsList,
                    )
                }
            } else {
                newsUiState
            }
        }
    }

    fun updateTargetNews(parsedArticle: ParsedArticle) {
        _newsUiState.update {
            it.copy(targetNews = parsedArticle)
        }
    }

    private fun Flow<ParsedNewsListData>.updateNewsUiState() = viewModelScope.launch {
        this@updateNewsUiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ParsedNewsListData(listOf(), Status.Loading)
        ).collect { parsedListData ->
            when (val currentStatus = parsedListData.status) {
                is Status.Success -> {
                    if (parsedListData.parsedArticle.isNullOrEmpty()) {
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
                                newsList = parsedListData.parsedArticle
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
                is Status.Loading -> {
                    _newsUiState.update {
                        it.copy(
                            isError = false,
                            isLoading = true,
                        )
                    }
                }
            }
        }
    }

}


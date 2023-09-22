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
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = "",
    val newsList: List<ParsedArticle> = listOf()
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _newsUiState: MutableStateFlow<NewsUiState> = MutableStateFlow(NewsUiState(isLoading = true))
    val newsUiState = _newsUiState.asStateFlow()

    fun getFreshNews(category: String) {
        if (category == NewsCategory.WHATEVER) {
            newsRepository.getWhateverNews().updateNewsUiState()
        } else {
            newsRepository.getSpecificNews(category).updateNewsUiState()
        }
    }

    private fun Flow<ParsedNewsListData>.updateNewsUiState() = viewModelScope.launch {
        this@updateNewsUiState.map { parsedListData ->
            when (val currentStatus = parsedListData.status) {
                is Status.Success -> {
                    if (parsedListData.parsedArticle.isNullOrEmpty()) {
                        NewsUiState(
                            isError = true,
                            errorMsg = "哇，目前沒有對應的新聞資料"
                        )
                    } else {
                        NewsUiState(newsList = parsedListData.parsedArticle)
                    }
                }
                is Status.ERROR -> {
                    NewsUiState(
                        isError = true,
                        errorMsg = currentStatus.msg
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NewsUiState(isLoading = true)
        ).collect {
            _newsUiState.value = it
        }
    }

}


package com.example.mvvmcomposenewsbrowser.ui.newsdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmcomposenewsbrowser.data.news.NewsRepository
import com.example.mvvmcomposenewsbrowser.data.news.ParsedArticle
import com.example.mvvmcomposenewsbrowser.data.news.Status
import com.example.mvvmcomposenewsbrowser.data.news.datasources.local.LocalLikedArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsDetailUiState(
    val newsLink: String,
    val isLiked: Boolean
)

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _newsDetailUiState: MutableStateFlow<NewsDetailUiState> = MutableStateFlow(NewsDetailUiState(newsLink = "", isLiked = false))
    val newsDetailUiState = _newsDetailUiState.asStateFlow()



}
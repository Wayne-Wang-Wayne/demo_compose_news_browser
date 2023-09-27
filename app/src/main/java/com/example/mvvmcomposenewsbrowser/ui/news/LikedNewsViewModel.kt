package com.example.mvvmcomposenewsbrowser.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmcomposenewsbrowser.data.news.NewsRepository
import com.example.mvvmcomposenewsbrowser.data.news.ParsedNews
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LikedNewsUiState(
    val likedNewsList: List<ParsedNews>,
    val targetNews: ParsedNews? = null
)

@HiltViewModel
class LikedNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _targetNews: MutableStateFlow<ParsedNews> = MutableStateFlow(ParsedNews("", "", "", "", "", true))

    private val _allLikedNews: Flow<List<ParsedNews>> = newsRepository
        .getAllLikedNews()

    val likedUiState = combine(_allLikedNews, _targetNews) { allLikedNews, targetNews ->
        LikedNewsUiState(
            likedNewsList = allLikedNews.reversed(),
            targetNews = targetNews
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LikedNewsUiState(listOf())
    )

    fun dislikeNews(parsedNews: ParsedNews) = viewModelScope.launch {
        newsRepository.dislikeNews(parsedNews)
    }

    fun toggleDetailLike(parsedNews: ParsedNews) {
        val isLiked = _targetNews.value.isLiked
        if(isLiked) {
            dislikeNews(parsedNews)
        } else {
            viewModelScope.launch {
                newsRepository.likeNews(parsedNews)
            }
        }
        _targetNews.update {
            it.copy(isLiked = !isLiked)
        }
    }

    fun targetNews(parsedNews: ParsedNews) {
        _targetNews.value = parsedNews
    }

}
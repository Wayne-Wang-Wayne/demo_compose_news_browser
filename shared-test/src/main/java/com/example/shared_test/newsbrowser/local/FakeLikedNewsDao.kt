package com.example.shared_test.newsbrowser.local

import com.example.democomposenewsbrowser.data.news.datasources.local.LikedNewsDao
import com.example.democomposenewsbrowser.data.news.datasources.local.LocalLikedNews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeLikedNewsDao : LikedNewsDao {

    private var _news: MutableMap<String, LocalLikedNews> = mutableMapOf()

    private val _newsFlow = MutableStateFlow(news)

    var news: List<LocalLikedNews>
        get() = _news.values.toList()
        set(newTasks) {
            _news = newTasks.associateBy { it.url }.toMutableMap()
            _newsFlow.value = newTasks
        }

    override suspend fun insert(localLikedNews: LocalLikedNews) {
        _news.apply {
            this[localLikedNews.url] = localLikedNews
            _newsFlow.emit(news)
        }
    }

    override fun isLiked(url: String): Boolean = _news.contains(url)

    override fun getLikedNews(): Flow<List<LocalLikedNews>> = _newsFlow.asStateFlow()

    override suspend fun delete(localLikedNews: LocalLikedNews) {
        _news.apply {
            val removedNews = this.remove(localLikedNews.url)
            if (removedNews != null) {
                _newsFlow.emit(news)
            }
        }
    }
}
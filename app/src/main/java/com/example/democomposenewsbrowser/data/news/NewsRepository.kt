package com.example.democomposenewsbrowser.data.news

import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getWhateverNews(): Flow<ParsedNewsListData>

    fun getSpecificNews(category: String): Flow<ParsedNewsListData>

    suspend fun likeNews(parsedNews: ParsedNews)

    suspend fun dislikeNews(parsedNews: ParsedNews)

    fun getAllLikedNews(): Flow<List<ParsedNews>>

}

data class ParsedNewsListData(
    val parsedNews: List<ParsedNews>?,
    val status: Status
)

data class ParsedNews(
    val author: String,
    val title: String,
    val url: String,
    val publishedAt: String,
    val imgUrl: String,
    var isLiked: Boolean
)

sealed interface Status {
    object Success: Status
    data class ERROR(val msg: String): Status
}
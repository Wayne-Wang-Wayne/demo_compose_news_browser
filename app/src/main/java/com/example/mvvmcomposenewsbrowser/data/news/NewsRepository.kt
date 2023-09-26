package com.example.mvvmcomposenewsbrowser.data.news

import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getWhateverNews(): Flow<ParsedNewsListData>

    fun getSpecificNews(category: String): Flow<ParsedNewsListData>

    suspend fun likeArticle(parsedArticle: ParsedArticle)

    suspend fun dislikeArticle(parsedArticle: ParsedArticle)

}

data class ParsedNewsListData(
    val parsedArticle: List<ParsedArticle>?,
    val status: Status
)

data class ParsedArticle(
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
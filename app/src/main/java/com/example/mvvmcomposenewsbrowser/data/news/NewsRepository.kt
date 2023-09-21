package com.example.mvvmcomposenewsbrowser.data.news

import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getWhateverNews(): Flow<RepoLevelData>

    fun getSpecificNews(): Flow<RepoLevelData>

}

data class RepoLevelData(
    val repoLevelArticle: List<RepoLevelArticle>?,
    val status: Status
)

data class RepoLevelArticle(
    val author: String,
    val title: String,
    val url: String,
    val publishedAt: String,
    val imgUrl: String,
    val isLiked: Boolean
)

sealed interface Status {
    object Success: Status
    data class ERROR(val msg: String): Status
}
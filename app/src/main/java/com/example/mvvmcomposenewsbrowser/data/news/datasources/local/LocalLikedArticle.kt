package com.example.mvvmcomposenewsbrowser.data.news.datasources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "liked_article"
)
data class LocalLikedArticle(
    val author: String,
    @PrimaryKey
    val title: String,
    val url: String,
    val publishedAt: String,
    val imgUrl: String
)
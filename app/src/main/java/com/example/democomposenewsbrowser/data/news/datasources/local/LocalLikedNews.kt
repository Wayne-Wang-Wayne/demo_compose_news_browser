package com.example.democomposenewsbrowser.data.news.datasources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "liked_article"
)
data class LocalLikedNews(
    val author: String,
    val title: String,
    @PrimaryKey
    val url: String,
    val publishedAt: String,
    val imgUrl: String
)
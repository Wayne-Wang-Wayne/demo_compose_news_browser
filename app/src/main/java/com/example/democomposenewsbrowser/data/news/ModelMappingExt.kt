package com.example.democomposenewsbrowser.data.news

import com.example.democomposenewsbrowser.data.news.datasources.local.LocalLikedNews
import com.example.democomposenewsbrowser.data.news.datasources.remote.Article

fun ParsedNews.toLocalLikedNews() = LocalLikedNews(
    author = this.author,
    title = this.title,
    url = this.url,
    publishedAt = this.publishedAt,
    imgUrl = this.imgUrl
)

fun LocalLikedNews.toParsedNews(isLiked: Boolean) = ParsedNews(
    author = this.author,
    title = this.title,
    url = this.url,
    publishedAt = this.publishedAt,
    imgUrl = this.imgUrl,
    isLiked = isLiked
)

fun Article.toParsedNews(
    url: String,
    imgUrl: String,
    isLiked: Boolean
) = ParsedNews(
    author = this.author,
    title = this.title,
    url = url,
    publishedAt = publishedAt,
    imgUrl = imgUrl,
    isLiked = isLiked
)
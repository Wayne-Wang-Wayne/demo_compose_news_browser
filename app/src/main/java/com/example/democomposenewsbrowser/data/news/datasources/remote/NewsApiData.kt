package com.example.democomposenewsbrowser.data.news.datasources.remote

data class NewsApiData(
    val status: String = "",
    val articles: List<Article>
)

data class Article(
    val author: String = "",
    val title: String = "",
    val url: String = "",
    val publishedAt: String = ""
)

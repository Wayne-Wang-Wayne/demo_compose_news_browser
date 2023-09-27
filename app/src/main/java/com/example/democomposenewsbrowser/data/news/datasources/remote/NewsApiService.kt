package com.example.democomposenewsbrowser.data.news.datasources.remote

import com.example.democomposenewsbrowser.BuildConfig
import com.example.democomposenewsbrowser.di.NEWS_URL_PATH
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET(NEWS_URL_PATH)
    fun getWhateverNews(
        @Query("country") country: String = "tw",
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY
    ): Flow<Response<NewsApiData>>

    @GET(NEWS_URL_PATH)
    fun getSpecificNews(
        @Query("country") country: String = "tw",
        @Query("category") category: String,
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY
    ): Flow<Response<NewsApiData>>

}

enum class NewsCategory(val tag: String) {
    WHATEVER("whatever"),
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology")
}

fun NewsCategory.toChinese() = when(this) {
    NewsCategory.WHATEVER -> "綜合"
    NewsCategory.BUSINESS -> "商業"
    NewsCategory.ENTERTAINMENT -> "娛樂"
    NewsCategory.HEALTH -> "健康"
    NewsCategory.SCIENCE -> "科學"
    NewsCategory.SPORTS -> "體育"
    NewsCategory.TECHNOLOGY -> "科技"
}
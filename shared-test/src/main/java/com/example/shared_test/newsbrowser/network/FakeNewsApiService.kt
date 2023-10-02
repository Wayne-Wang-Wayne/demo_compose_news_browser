package com.example.shared_test.newsbrowser.network

import com.example.democomposenewsbrowser.data.news.datasources.remote.Article
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsApiData
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeNewsApiService : NewsApiService {
    override fun getWhateverNews(
        country: String,
        apiKey: String
    ): Flow<Response<NewsApiData>> = flow {
        emit(fakeNewsApiService[country] ?: throw java.lang.Exception("Get news from api error!"))
    }

    override fun getSpecificNews(
        country: String,
        category: String,
        apiKey: String
    ): Flow<Response<NewsApiData>> = flow {
        emit(fakeNewsApiService[country] ?: throw java.lang.Exception("Get news from api error!"))
    }
}

const val FAKE_NEWS_API_SUCCESS_KEY = "FAKE_NEWS_API_SUCCESS_KEY"
const val FAKE_NEWS_API_FAIL_KEY = "FAKE_FAIL_COUNTRY_KEY"

val FAKE_NEWS_API_SUCCESS_DATA: Response<NewsApiData> = Response.success(
    NewsApiData(
        "Success",
        listOf(
            Article(
                author = "author1",
                title = "title1",
                url = "url1",
                publishedAt = "publishedAt1"
            ),
            Article(
                author = "author2",
                title = "title2",
                url = "url2",
                publishedAt = "publishedAt2"
            ),
        )
    )
)

val FAKE_NEWS_API_FAIL_DATA: Response<NewsApiData> = Response.error(
    -1,
    "It is an API error".toResponseBody()
)

val fakeNewsApiService = mapOf(
    FAKE_NEWS_API_SUCCESS_KEY to FAKE_NEWS_API_SUCCESS_DATA,
    FAKE_NEWS_API_FAIL_KEY to FAKE_NEWS_API_FAIL_DATA
)
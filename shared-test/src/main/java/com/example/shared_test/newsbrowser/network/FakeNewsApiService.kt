package com.example.shared_test.newsbrowser.network

import com.example.democomposenewsbrowser.data.news.datasources.remote.Article
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsApiData
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

const val FAKE_NEWS_API_SUCCESS_KEY = "FAKE_NEWS_API_SUCCESS_KEY"
const val FAKE_NEWS_API_ERROR_KEY = "FAKE_NEWS_API_ERROR_KEY"

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
    404,
    "It is an API error".toResponseBody()
)

val fakeNewsApiService = mapOf(
    FAKE_NEWS_API_SUCCESS_KEY to FAKE_NEWS_API_SUCCESS_DATA,
    FAKE_NEWS_API_ERROR_KEY to FAKE_NEWS_API_FAIL_DATA
)

class FakeNewsApiService : NewsApiService {

    var forceException = false

    var forceError = false

    override fun getWhateverNews(
        country: String,
        apiKey: String
    ): Flow<Response<NewsApiData>> = flow {
        if (forceException) throw java.lang.Exception("Get news from api error!")
        if(forceError) emit(fakeNewsApiService[FAKE_NEWS_API_ERROR_KEY] ?: throw java.lang.Exception("Get news from api error!"))
        else emit(fakeNewsApiService[FAKE_NEWS_API_SUCCESS_KEY] ?: throw java.lang.Exception("Get news from api error!"))
    }

    override fun getSpecificNews(
        country: String,
        category: String,
        apiKey: String
    ): Flow<Response<NewsApiData>> = flow {
        if (forceException) throw java.lang.Exception("Get news from api error!")
        if(forceError) emit(fakeNewsApiService[FAKE_NEWS_API_ERROR_KEY] ?: throw java.lang.Exception("Get news from api error!"))
        else emit(fakeNewsApiService[FAKE_NEWS_API_SUCCESS_KEY] ?: throw java.lang.Exception("Get news from api error!"))
    }
}
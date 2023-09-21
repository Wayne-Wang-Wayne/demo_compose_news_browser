package com.example.mvvmcomposenewsbrowser.data.news

import com.example.mvvmcomposenewsbrowser.data.news.datasources.local.LikedArticleDao
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsApiData
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsApiService
import com.example.mvvmcomposenewsbrowser.di.ApplicationScope
import com.example.mvvmcomposenewsbrowser.di.DefaultDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val likedArticleDao: LikedArticleDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): NewsRepository {

    override fun getWhateverNews(): Flow<ParsedNewsListData> =
        newsApiService.getWhateverNews().toParsedNews()

    override fun getSpecificNews(category: String): Flow<ParsedNewsListData> =
        newsApiService.getSpecificNews(category = category).toParsedNews()

    private fun Flow<Response<NewsApiData>>.toParsedNews() = map { response ->
        val articles = response.body()?.articles
        if (articles.isNullOrEmpty()) {
            ParsedNewsListData(null, Status.ERROR(response.message()))
        } else {
            val parsedArticle = articles.map { article ->
                ParsedArticle(
                    author = article.author,
                    title = article.title,
                    url = article.url,
                    publishedAt = article.publishedAt,
                    imgUrl = "",
                    isLiked = likedArticleDao.isLiked(article.title)
                )
            }
            ParsedNewsListData(parsedArticle, Status.Success)
        }
    }.catch {
        emit(ParsedNewsListData(null, Status.ERROR(it.message ?: "")))
    }.flowOn(dispatcher)

}


package com.example.mvvmcomposenewsbrowser.data.news

import com.example.mvvmcomposenewsbrowser.data.news.datasources.local.LikedArticleDao
import com.example.mvvmcomposenewsbrowser.data.news.datasources.local.LocalLikedArticle
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsApiData
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsApiService
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.UrlBasicInfoService
import com.example.mvvmcomposenewsbrowser.di.ApplicationScope
import com.example.mvvmcomposenewsbrowser.di.DefaultDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val likedArticleDao: LikedArticleDao,
    private val urlBasicInfoService: UrlBasicInfoService,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): NewsRepository {

    override fun getWhateverNews(): Flow<ParsedNewsListData> =
        newsApiService.getWhateverNews().apiToParsedNews()

    override fun getSpecificNews(category: String): Flow<ParsedNewsListData> =
        newsApiService.getSpecificNews(category = category).apiToParsedNews()

    override suspend fun likeArticle(parsedArticle: ParsedArticle) = withContext(dispatcher) {
        likedArticleDao.insert(
            LocalLikedArticle(
                author = parsedArticle.author,
                title = parsedArticle.title,
                url = parsedArticle.url,
                publishedAt = parsedArticle.publishedAt,
                imgUrl = parsedArticle.imgUrl
            )
        )
    }

    override suspend fun dislikeArticle(parsedArticle: ParsedArticle) = withContext(dispatcher) {
        likedArticleDao.delete(
            LocalLikedArticle(
                author = parsedArticle.author,
                title = parsedArticle.title,
                url = parsedArticle.url,
                publishedAt = parsedArticle.publishedAt,
                imgUrl = parsedArticle.imgUrl
            )
        )
    }

    private fun Flow<Response<NewsApiData>>.apiToParsedNews() = map { response ->
        val articles = response.body()?.articles
        if (articles.isNullOrEmpty()) {
            ParsedNewsListData(null, Status.ERROR(response.message()))
        } else {
            coroutineScope {
                val parsedArticle = articles.map { article ->
                    async {
                        val linkBasicInfo = urlBasicInfoService.getLinkBasicInfo(article.url)
                        ParsedArticle(
                            author = article.author,
                            title = article.title,
                            url = linkBasicInfo.redirectUrl?.encodeUrl() ?: article.url.encodeUrl(),
                            publishedAt = article.publishedAt,
                            imgUrl = linkBasicInfo.image ?: "",
                            isLiked = likedArticleDao.isLiked(article.url)
                        )
                    }
                }
                ParsedNewsListData(parsedArticle.awaitAll(), Status.Success)
            }
        }
    }.catch {
        emit(ParsedNewsListData(null, Status.ERROR(it.message ?: "")))
    }.flowOn(dispatcher)

    private fun Flow<List<LocalLikedArticle>>.localToParsedNews() = map { article ->
        ParsedNewsListData(
            listOf(
                ParsedArticle(
                    author = article.first().author,
                    title = article.first().title,
                    url = article.first().url,
                    publishedAt = article.first().publishedAt,
                    imgUrl = article.first().imgUrl,
                    isLiked = likedArticleDao.isLiked(article.first().url)
                )
            ),
            Status.Success
        )
    }.catch {
        emit(ParsedNewsListData(null, Status.ERROR(it.message ?: "")))
    }.flowOn(dispatcher)

}

fun String.encodeUrl(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8.toString())



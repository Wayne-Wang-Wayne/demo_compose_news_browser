package com.example.democomposenewsbrowser.data.news

import com.example.democomposenewsbrowser.data.news.datasources.local.LikedNewsDao
import com.example.democomposenewsbrowser.data.news.datasources.local.LocalLikedNews
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsApiData
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsApiService
import com.example.democomposenewsbrowser.data.news.datasources.remote.UrlBasicInfoService
import com.example.democomposenewsbrowser.di.ApplicationScope
import com.example.democomposenewsbrowser.di.DefaultDispatcher
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
    private val likedNewsDao: LikedNewsDao,
    private val urlBasicInfoService: UrlBasicInfoService,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): NewsRepository {

    override fun getWhateverNews(): Flow<ParsedNewsListData> =
        newsApiService.getWhateverNews().apiToParsedNews()

    override fun getSpecificNews(category: String): Flow<ParsedNewsListData> =
        newsApiService.getSpecificNews(category = category).apiToParsedNews()

    override suspend fun likeNews(parsedNews: ParsedNews) = withContext(dispatcher) {
        likedNewsDao.insert(parsedNews.toLocalLikedNews())
    }

    override suspend fun dislikeNews(parsedNews: ParsedNews) = withContext(dispatcher) {
        likedNewsDao.delete(parsedNews.toLocalLikedNews())
    }

    override fun getAllLikedNews(): Flow<List<ParsedNews>> = likedNewsDao.getLikedNews().map { localLikedNewsList ->
        localLikedNewsList.map { localLikedNews ->
            localLikedNews.toParsedNews(isLiked = true)
        }
    }.catch {
        emit(listOf())
    }.flowOn(dispatcher)

    private fun Flow<Response<NewsApiData>>.apiToParsedNews() = map { response ->
        val articles = response.body()?.articles
        if (articles.isNullOrEmpty()) {
            ParsedNewsListData(null, Status.ERROR(response.message()))
        } else {
            coroutineScope {
                val parsedNews = articles.map { article ->
                    async {
                        val linkBasicInfo = urlBasicInfoService.getLinkBasicInfo(article.url)
                        val url = linkBasicInfo.redirectUrl ?: article.url
                        val imgUrl = linkBasicInfo.image ?: ""
                        val isLiked = likedNewsDao.isLiked(url)
                        article.toParsedNews(
                            url = url,
                            imgUrl = imgUrl,
                            isLiked = isLiked
                        )
                    }
                }
                ParsedNewsListData(parsedNews.awaitAll(), Status.Success)
            }
        }
    }.catch {
        emit(ParsedNewsListData(null, Status.ERROR(it.message ?: "")))
    }.flowOn(dispatcher)

}

fun String.encodeUrl(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8.toString())



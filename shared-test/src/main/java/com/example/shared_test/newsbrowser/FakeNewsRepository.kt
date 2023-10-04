package com.example.shared_test.newsbrowser

import com.example.democomposenewsbrowser.data.news.NewsRepository
import com.example.democomposenewsbrowser.data.news.ParsedNews
import com.example.democomposenewsbrowser.data.news.ParsedNewsListData
import com.example.democomposenewsbrowser.data.news.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

val fakeRemoteNewsData = listOf(
    ParsedNews(
        author = "author1",
        title = "title1",
        url = "url1",
        publishedAt = "publishedAt1",
        imgUrl = "imgUrl1",
        isLiked = false
    ),
    ParsedNews(
        author = "author2",
        title = "title2",
        url = "url2",
        publishedAt = "publishedAt2",
        imgUrl = "imgUrl2",
        isLiked = false
    ),
)

class FakeNewsRepository : NewsRepository {

    companion object {
        const val FAKE_ERROR_MESSAGE = "FAKE_ERROR_MESSAGE"
    }

    private var forceError = false

    private val _savedNews = MutableStateFlow(LinkedHashMap<String, ParsedNews>())
    private val _savedNewsList = _savedNews.map {
        it.values.toList().map { parsedNews ->
            parsedNews.copy(isLiked = true)
        }
    }

    override fun getWhateverNews(): Flow<ParsedNewsListData> = flow {
        // emulate delay
        delay(3000)
        if (forceError) {
            emit(
                ParsedNewsListData(
                    parsedNews = listOf(),
                    status = Status.ERROR(FAKE_ERROR_MESSAGE)
                )
            )
        } else {
            emit(
                ParsedNewsListData(
                    parsedNews = fakeRemoteNewsData.map { parsedNews ->
                        parsedNews.setCorrectParsedNews()
                    },
                    status = Status.Success
                )
            )
        }
    }

    override fun getSpecificNews(category: String): Flow<ParsedNewsListData> = flow {
        // emulate delay
        delay(3000)
        if (forceError) {
            emit(
                ParsedNewsListData(
                    parsedNews = listOf(),
                    status = Status.ERROR(FAKE_ERROR_MESSAGE)
                )
            )
        } else {
            emit(
                ParsedNewsListData(
                    parsedNews = fakeRemoteNewsData.map { parsedNews ->
                        parsedNews.setCorrectParsedNews()
                    },
                    status = Status.Success
                )
            )
        }
    }

    override suspend fun likeNews(parsedNews: ParsedNews) {
        if (forceError) return
        _savedNews.update {
            it[parsedNews.url] = parsedNews
            it
        }
    }

    override suspend fun dislikeNews(parsedNews: ParsedNews) {
        if (forceError) return
        _savedNews.update {
            it.remove(parsedNews.url)
            it
        }
    }

    override fun getAllLikedNews(): Flow<List<ParsedNews>> = _savedNewsList

    fun setForceError(forceError: Boolean) {
        this.forceError = forceError
    }

    private fun ParsedNews.setCorrectParsedNews() =
        copy(isLiked = _savedNews.value.values.toList().map { it.url }.contains(this.url))

}
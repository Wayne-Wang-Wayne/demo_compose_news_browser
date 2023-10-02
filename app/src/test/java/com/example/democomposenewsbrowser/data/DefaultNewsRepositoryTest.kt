package com.example.democomposenewsbrowser.data

import com.example.democomposenewsbrowser.data.news.DefaultNewsRepository
import com.example.democomposenewsbrowser.data.news.ParsedNews
import com.example.democomposenewsbrowser.data.news.Status
import com.example.shared_test.newsbrowser.local.FakeLikedNewsDao
import com.example.shared_test.newsbrowser.network.FakeNewsApiService
import com.example.shared_test.newsbrowser.network.FakeUrlBasicInfoService
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultNewsRepositoryTest {

    private lateinit var newsApiService: FakeNewsApiService
    private lateinit var likedNewsDao: FakeLikedNewsDao
    private lateinit var urlBasicInfoService: FakeUrlBasicInfoService


    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    private lateinit var defaultNewsRepository: DefaultNewsRepository

    @Before
    fun createRepository() {
        newsApiService = FakeNewsApiService()
        likedNewsDao = FakeLikedNewsDao()
        urlBasicInfoService = FakeUrlBasicInfoService()
        defaultNewsRepository = DefaultNewsRepository(
            newsApiService = newsApiService,
            likedNewsDao = likedNewsDao,
            urlBasicInfoService = urlBasicInfoService,
            dispatcher = testDispatcher,
            scope = testScope
        )
    }

    @Test
    fun defaultNewsRepository_getDefaultNewsSuccessWithBasicInfo_shouldReturnSuccessResultWithBasicInfoUpdated() = testScope.runTest {
        newsApiService.forceFail = false
        val result = defaultNewsRepository.getWhateverNews().first()
        val parsedNewsList = result.parsedNews
        assertEquals(result.status, Status.Success)
        assertEquals(
            parsedNewsList!![1], ParsedNews(
                author = "author2",
                title = "title2",
                url = "redirectUrl2",
                publishedAt = "publishedAt2",
                imgUrl = "image2",
                isLiked = false
            )
        )
    }

    @Test
    fun defaultNewsRepository_getDefaultNewsSuccessWithNoBasicInfo_shouldReturnSuccessResultWithNoBasicInfoUpdated() = testScope.runTest {
        newsApiService.forceFail = false
        val result = defaultNewsRepository.getWhateverNews().first()
        val parsedNewsList = result.parsedNews
        assertEquals(result.status, Status.Success)
        assertEquals(
            parsedNewsList!![0], ParsedNews(
                author = "author1",
                title = "title1",
                url = "url1",
                publishedAt = "publishedAt1",
                imgUrl = "",
                isLiked = false
            )
        )
    }

    @Test
    fun defaultNewsRepository_getDefaultNewsSuccess_shouldReturnSuccessResult() = testScope.runTest {
        newsApiService.forceFail = false

    }

}
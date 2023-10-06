package com.example.democomposenewsbrowser.news


import com.example.democomposenewsbrowser.data.news.ParsedNews
import com.example.democomposenewsbrowser.ui.news.LikedNewsViewModel
import com.example.shared_test.newsbrowser.FakeNewsRepository
import com.example.shared_test.newsbrowser.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LikedNewsViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    val myNews = ParsedNews(
        author = "author1",
        title = "title1",
        url = "url1",
        publishedAt = "publishedAt1",
        imgUrl = "imgUrl1",
        isLiked = false
    )

    private lateinit var likedNewsViewModel: LikedNewsViewModel

    private lateinit var newsRepository: FakeNewsRepository

    @Before
    fun setupLikedNewsViewModel() {
        newsRepository = FakeNewsRepository()
        likedNewsViewModel = LikedNewsViewModel(newsRepository)
    }

    @Test
    fun likedNewsViewModelTest_dislikedNews_shouldUpdateUiStateAndRepository() = runTest {
        val uiStateFlow = likedNewsViewModel.likedUiState
        insertOneLikeNews()
        val firstUILikedNews = uiStateFlow.first().likedNewsList
        // 先確認UIState有一個喜愛新聞
        assertEquals(myNews.url, firstUILikedNews[0].url)
        // 再確認repository
        val firstStoredLikedNews = newsRepository.getAllLikedNews().first()
        assertEquals(myNews.url, firstStoredLikedNews[0].url)
        likedNewsViewModel.dislikeNews(myNews)
        val secondUILikedNews = uiStateFlow.first().likedNewsList
        // 先確認UIState喜歡list是空的
        assertTrue(secondUILikedNews.isEmpty())
        // 再確認repository是空的
        val secondStoredLikedNews = newsRepository.getAllLikedNews().first()
        assertTrue(secondStoredLikedNews.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun likedNewsViewModelTest_toggleDetailLike_shouldUpdateUiStateAndRepository() = runTest {
        val uiStateFlow = likedNewsViewModel.likedUiState
        insertOneLikeNews()
        val firstUILikedNews = uiStateFlow.first().likedNewsList
        // 先確認UIState有一個喜愛新聞
        assertEquals(myNews.url, firstUILikedNews[0].url)
        // 再確認repository
        val firstStoredLikedNews = newsRepository.getAllLikedNews().first()
        assertEquals(myNews.url, firstStoredLikedNews[0].url)
        likedNewsViewModel.targetNews(myNews)
        // toggle
        likedNewsViewModel.toggleDetailLike(myNews)
        val secondUILikedNews = uiStateFlow.first().likedNewsList
        val secondUITargetNews = uiStateFlow.first().targetNews
        // 先確認UIState喜歡list是空的
        assertTrue(secondUILikedNews.isEmpty())
        //確認target news變成不喜歡
        assertFalse(secondUITargetNews!!.isLiked)
        // 再確認repository是空的
        val secondStoredLikedNews = newsRepository.getAllLikedNews().first()
        assertTrue(secondStoredLikedNews.isEmpty())
        // 再次toggle
        likedNewsViewModel.toggleDetailLike(myNews)
        advanceUntilIdle()
        val thirdUILikedNews = uiStateFlow.first().likedNewsList
        val thirdUITargetNews = uiStateFlow.first().targetNews
        // 先確認UIState有一個喜愛新聞
        assertEquals(myNews.url, thirdUILikedNews[0].url)
        //確認target news變成喜歡
        assertTrue(thirdUITargetNews!!.isLiked)
        // 再確認repository
        val thirdStoredLikedNews = newsRepository.getAllLikedNews().first()
        assertEquals(myNews.url, thirdStoredLikedNews[0].url)
    }

    @Test
    fun likedNewsViewModelTest_toggleDetailLikeOnUnLikedNews_shouldLikeNews() {

    }

    private suspend fun insertOneLikeNews() {
        newsRepository.likeNews(myNews)
    }


}
package com.example.democomposenewsbrowser.news

import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsCategory
import com.example.democomposenewsbrowser.ui.news.NewsViewModel
import com.example.shared_test.newsbrowser.FakeNewsRepository
import com.example.shared_test.newsbrowser.MainCoroutineRule
import com.example.shared_test.newsbrowser.fakeRemoteNewsData
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var newsViewModel: NewsViewModel

    private lateinit var newsRepository: FakeNewsRepository

    @Before
    fun setupNewsViewModel() {
        newsRepository = FakeNewsRepository()
        newsViewModel = NewsViewModel(newsRepository)
    }

    @Test
    fun newsViewModel_getWhateverFreshNewsSuccess_shouldUpdateUIState() = runTest {
        val newsUiStateFlow = newsViewModel.newsUiState
        newsViewModel.getFreshNews(NewsCategory.WHATEVER)
        assertTrue(newsUiStateFlow.value.isLoading)
        // 直接跑完所有pending的 coroutines actions
        advanceUntilIdle()
        val successUiState = newsUiStateFlow.first()
        assertEquals(successUiState.newsCategory, NewsCategory.WHATEVER)
        assertFalse(successUiState.isLoading)
        assertFalse(successUiState.isError)
        assertEquals(successUiState.errorMsg, "")
        assertEquals(successUiState.newsList.toList(), fakeRemoteNewsData)
        assertNull(successUiState.targetNews)
    }

    @Test
    fun newsViewModel_getWhateverFreshNewsFail_shouldUpdateUIState() = runTest {
        newsRepository.setForceError(true)
        val newsUiStateFlow = newsViewModel.newsUiState
        newsViewModel.getFreshNews(NewsCategory.WHATEVER)
        assertTrue(newsUiStateFlow.value.isLoading)
        // 直接跑完所有pending的 coroutines actions
        advanceUntilIdle()
        val successUiState = newsUiStateFlow.first()
        assertEquals(successUiState.newsCategory, NewsCategory.WHATEVER)
        assertFalse(successUiState.isLoading)
        assertTrue(successUiState.isError)
        assertTrue(successUiState.errorMsg.isNotEmpty())
        assertTrue(successUiState.newsList.isEmpty())
        assertNull(successUiState.targetNews)
    }

    @Test
    fun newsViewModel_getSpecificFreshNewsSuccess_shouldUpdateUIState() = runTest {
        val newsUiStateFlow = newsViewModel.newsUiState
        newsViewModel.getFreshNews(NewsCategory.ENTERTAINMENT)
        assertTrue(newsUiStateFlow.value.isLoading)
        // 直接跑完所有pending的 coroutines actions
        advanceUntilIdle()
        val successUiState = newsUiStateFlow.first()
        assertEquals(successUiState.newsCategory, NewsCategory.ENTERTAINMENT)
        assertFalse(successUiState.isLoading)
        assertFalse(successUiState.isError)
        assertEquals(successUiState.errorMsg, "")
        assertEquals(successUiState.newsList.toList(), fakeRemoteNewsData)
        assertNull(successUiState.targetNews)
    }

    @Test
    fun newsViewModel_getSpecificFreshNewsFail_shouldUpdateUIState() = runTest {
        newsRepository.setForceError(true)
        val newsUiStateFlow = newsViewModel.newsUiState
        newsViewModel.getFreshNews(NewsCategory.ENTERTAINMENT)
        assertTrue(newsUiStateFlow.value.isLoading)
        // 直接跑完所有pending的 coroutines actions
        advanceUntilIdle()
        val successUiState = newsUiStateFlow.first()
        assertEquals(successUiState.newsCategory, NewsCategory.ENTERTAINMENT)
        assertFalse(successUiState.isLoading)
        assertTrue(successUiState.isError)
        assertTrue(successUiState.errorMsg.isNotEmpty())
        assertTrue(successUiState.newsList.isEmpty())
        assertNull(successUiState.targetNews)
    }

    @Test
    fun newsViewModel_toggleUnlikedNews_uiStateShouldUpdateAndRepositoryShouldUpdate() = runTest {
        val newsUiStateFlow = newsViewModel.newsUiState
        newsViewModel.getFreshNews(NewsCategory.WHATEVER)
        // 直接跑完所有pending的 coroutines actions
        advanceUntilIdle()
        val originNewsList = newsUiStateFlow.first().newsList.toList()
        // 先檢查uiState正確
        assertFalse(originNewsList[0].isLiked)
        assertFalse(originNewsList[1].isLiked)
        // 再檢查repository正確
        val storeNews = newsRepository.getAllLikedNews().first()
        assertTrue(storeNews.isEmpty())
        // 第0筆觸發收藏
        newsViewModel.toggleLike(originNewsList[0])
        val afterNewsList = newsUiStateFlow.first().newsList.toList()
        // 先檢查state正確
        assertTrue(afterNewsList[0].isLiked)
        assertFalse(afterNewsList[1].isLiked)
        // 再檢查repository正確更新
        val secondTimeStoreNews = newsRepository.getAllLikedNews().first()
        assertEquals(afterNewsList[0].url, secondTimeStoreNews[0].url)
    }

}
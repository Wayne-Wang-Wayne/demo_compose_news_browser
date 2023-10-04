package com.example.democomposenewsbrowser.news

import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsCategory
import com.example.democomposenewsbrowser.ui.news.NewsViewModel
import com.example.shared_test.newsbrowser.FakeNewsRepository
import com.example.shared_test.newsbrowser.MainCoroutineRule
import com.example.shared_test.newsbrowser.fakeRemoteNewsData
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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

}
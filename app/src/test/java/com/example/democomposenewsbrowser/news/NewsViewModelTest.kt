package com.example.democomposenewsbrowser.news

import com.example.democomposenewsbrowser.ui.news.NewsViewModel
import com.example.shared_test.newsbrowser.FakeNewsRepository
import com.example.shared_test.newsbrowser.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

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

}
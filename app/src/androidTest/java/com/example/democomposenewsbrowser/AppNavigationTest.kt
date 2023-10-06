package com.example.democomposenewsbrowser

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.democomposenewsbrowser.data.news.NewsRepository
import com.example.democomposenewsbrowser.ui.MyNavGraph
import com.example.democomposenewsbrowser.ui.theme.DemoComposeNewsBrowserTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AppNavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var newsRepository: NewsRepository

    @Before
    fun init() {
        hiltRule.inject()
        setContent()
    }

    @Test
    fun xxx() {
        newsRepository.getAllLikedNews()
        newsRepository
        Log.d("","")
    }

    private fun setContent() {
        composeTestRule.setContent {
            DemoComposeNewsBrowserTheme {
                MyNavGraph()
            }
        }
    }

    private fun openDrawer() {
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.open_drawer))
            .performClick()
    }

}
package com.example.democomposenewsbrowser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.democomposenewsbrowser.data.news.NewsRepository
import com.example.democomposenewsbrowser.ui.MyNavGraph
import com.example.democomposenewsbrowser.ui.theme.DemoComposeNewsBrowserTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
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
        assertTrue(true)
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
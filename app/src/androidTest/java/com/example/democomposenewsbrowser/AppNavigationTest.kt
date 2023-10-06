package com.example.democomposenewsbrowser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.democomposenewsbrowser.data.news.NewsRepository
import com.example.democomposenewsbrowser.ui.MyNavGraph
import com.example.democomposenewsbrowser.ui.theme.DemoComposeNewsBrowserTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class AppNavigationTest {

    private val TAG: String = this.javaClass.simpleName

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
    fun firstOpenApp_shouldGoToNewsListScreen() {
        composeTestRule.printUnmergedTree(TAG)
        assertIsNewsListScreen()
    }

    @Test
    fun clickNewsListItem_shouldGoToNewsDetail() {
        goToNewsDetail()
        composeTestRule.onNodeWithText(activity.getString(R.string.news_detail_title))
            .assertIsDisplayed()
    }

    @Test
    fun clickNewsDetailBack_shouldGoToNewsList() {
        goToNewsDetail()
        composeTestRule.onNodeWithText(activity.getString(R.string.news_detail_title))
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.back_press)).performClick()
        assertIsNewsListScreen()
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

    private fun goToNewsDetail() {
        composeTestRule.waitUntilAtLeastOneExists(hasText("title1"), 5000)
        composeTestRule.onNodeWithText("title1").performClick()
    }

    private fun assertIsNewsListScreen() {
        composeTestRule.onNode(hasAnySibling(hasContentDescription(activity.getString(R.string.open_drawer))))
            .assertIsDisplayed()
    }

}
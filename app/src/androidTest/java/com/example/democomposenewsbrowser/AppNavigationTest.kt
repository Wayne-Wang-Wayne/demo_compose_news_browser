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
        // 確認一開始在NewsList頁
        assertIsNewsListScreen()
    }

    @Test
    fun clickNewsListItem_shouldGoToNewsDetail() {
        // 點擊第一個item
        composeTestRule.waitUntilAtLeastOneExists(hasText("title1"), 5000)
        composeTestRule.onNodeWithText("title1").performClick()
        // 確認在NewsDetail頁
        composeTestRule.onNodeWithText(activity.getString(R.string.news_detail_title))
            .assertIsDisplayed()
    }

    @Test
    fun selectDrawerLikedNewsListScreen_shouldGoToLikedNewsListScreen() {
        goToLikedNewsScreen()
        // 確認在收藏新聞頁
        assertIsLikedNewsListScreen()
    }

    @Test
    fun selectDrawerNewsListScreen_shouldGoToNewsListScreen() {
        goToLikedNewsScreen()
        // 確認在收藏新聞頁
        assertIsLikedNewsListScreen()
        // 點擊drawer按鈕回到NewsList
        openDrawer()
        composeTestRule.onNodeWithText(activity.getString(R.string.news_list_title))
            .performClick()
        // 確認在NewsListScreen
        assertIsNewsListScreen()
    }

    @Test
    fun clickLikedNewsListItem_shouldGoToNewsDetail() {
        // 先按愛心讓收藏有item按
        composeTestRule.waitUntilAtLeastOneExists(hasText("title1"), 5000)
        // 按第一個愛心
        composeTestRule.onNode(
            hasParent(hasText("title1")).and(
                hasContentDescription(activity.getString(R.string.like_button))
            )
        ).performClick()
        // 去收藏新聞頁
        goToLikedNewsScreen()
        // 點收藏item
        composeTestRule.onNodeWithText("title1").performClick()
        // 確認在NewsDetail頁
        composeTestRule.onNodeWithText(activity.getString(R.string.news_detail_title))
            .assertIsDisplayed()
    }

    @Test
    fun clickNewsDetailBack_shouldGoToPreviousScreen() {
        // 先按愛心讓收藏有item按
        composeTestRule.waitUntilAtLeastOneExists(hasText("title1"), 5000)
        composeTestRule.onNode(
            hasParent(hasText("title1")).and(
                hasContentDescription(activity.getString(R.string.like_button))
            )
        ).performClick()
        // 去第一個新聞
        composeTestRule.onNodeWithText("title1").performClick()
        // 確認在NewsDetail頁
        composeTestRule.onNodeWithText(activity.getString(R.string.news_detail_title))
            .assertIsDisplayed()
        // 點擊返回
        backPress()
        // 確認回到NewsList頁
        assertIsNewsListScreen()
        // 去LikedNews頁
        goToLikedNewsScreen()
        // 點擊第一個收藏新聞
        composeTestRule.onNodeWithText("title1").performClick()
        // 確認在NewsDetail頁
        composeTestRule.onNodeWithText(activity.getString(R.string.news_detail_title))
            .assertIsDisplayed()
        // 點擊返回
        backPress()
        // 確認在收藏新聞頁
        assertIsLikedNewsListScreen()
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

    private fun assertIsNewsListScreen() {
        composeTestRule.onNode(
            hasText(activity.getString(R.string.news_list_title)).and(
                hasAnySibling(hasContentDescription(activity.getString(R.string.open_drawer)))
            )
        ).assertIsDisplayed()
    }

    private fun assertIsLikedNewsListScreen() {
        composeTestRule.onNode(
            hasText(activity.getString(R.string.liked_news_title)).and(
                hasAnySibling(hasContentDescription(activity.getString(R.string.open_drawer)))
            )
        ).assertIsDisplayed()
    }

    private fun backPress() {
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.back_press)).performClick()
    }

    private fun goToLikedNewsScreen() {
        openDrawer()
        composeTestRule.onNodeWithText(activity.getString(R.string.liked_news_title))
            .performClick()
    }

}
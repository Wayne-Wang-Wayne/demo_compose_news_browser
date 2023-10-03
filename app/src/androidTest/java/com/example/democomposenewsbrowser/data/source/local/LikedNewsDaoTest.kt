package com.example.democomposenewsbrowser.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.filters.SmallTest
import com.example.democomposenewsbrowser.data.news.datasources.local.LocalLikedNews
import com.example.democomposenewsbrowser.data.news.datasources.local.NewsDatabase
import junit.framework.Assert.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@SmallTest
class LikedNewsDaoTest {

    private lateinit var database: NewsDatabase

    private val likedNewsList = listOf(
        LocalLikedNews(
            author = "author1",
            title = "title1",
            url = "url1",
            publishedAt = "publishedAt1",
            imgUrl = "imgUrl1"
        ),
        LocalLikedNews(
            author = "author2",
            title = "title2",
            url = "url2",
            publishedAt = "publishedAt2",
            imgUrl = "imgUrl2"
        )
    )

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            NewsDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun likedNewsDaoTest_insertNewsAndGetLikedNews_shouldGetCorrectNews() = runTest {
        val news = likedNewsList[0]
        database.likedArticleDao().insert(news)
        val likedNews = database.likedArticleDao().getLikedNews()
        assertEquals(likedNews.first()[0], news)
    }

    @Test
    fun likedNewsDaoTest_insertNewsAndCheckIsLiked_shouldGetIsLikedTrue() = runTest {
        val news = likedNewsList[0]
        database.likedArticleDao().insert(news)
        val isLiked = database.likedArticleDao().isLiked(news.url)
        assertTrue(isLiked)
    }

    @Test
    fun likedNewsDaoTest_insertNewsAndCheckIsLikedForAnother_shouldGetIsLikedFalse() = runTest {
        val news = likedNewsList[0]
        database.likedArticleDao().insert(news)
        val isLiked = database.likedArticleDao().isLiked("not existed news url")
        assertFalse(isLiked)
    }

    @Test
    fun likedNewsDaoTest_insertNewsAndDeleteThatNews_shouldGetCorrectNewsResult() = runTest {
        val dao = database.likedArticleDao()
        dao.insert(likedNewsList[0])
        dao.insert(likedNewsList[1])
        val likedList1 = dao.getLikedNews().first()
        assertTrue(likedList1.contains(likedNewsList[0]))
        assertTrue(likedList1.contains(likedNewsList[1]))
        dao.delete(likedNewsList[0])
        val likedList2 = dao.getLikedNews().first()
        assertFalse(likedList2.contains(likedNewsList[0]))
        assertTrue(likedList2.contains(likedNewsList[1]))
    }

    @Test
    fun likedNewsDaoTest_insertNewsAndDeleteAnotherNews_shouldGetCorrectNewsResult() = runTest {
        val dao = database.likedArticleDao()
        dao.insert(likedNewsList[0])
        dao.delete(likedNewsList[1])
        val likedList = dao.getLikedNews().first()
        assertTrue(likedList.contains(likedNewsList[0]))
        assertFalse(likedList.contains(likedNewsList[1]))
    }

}
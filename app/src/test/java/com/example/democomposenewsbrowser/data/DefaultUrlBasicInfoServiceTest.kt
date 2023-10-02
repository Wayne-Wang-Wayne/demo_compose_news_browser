package com.example.democomposenewsbrowser.data


import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.AGENT
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.CSS_QUERY
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.DOC_SELECT_QUERY
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.OG_DESCRIPTION
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.OG_IMAGE
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.OG_SITE_NAME
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.OG_TITLE
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.OG_URL
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.OPEN_GRAPH_KEY
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.PROPERTY
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.REFERRER
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService.Companion.TIMEOUT
import com.example.democomposenewsbrowser.util.JsoupWrapper
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.jsoup.Connection
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.junit.Test
import org.mockito.Mockito.*


class DefaultUrlBasicInfoServiceTest {

    private lateinit var defaultUrlBasicInfoService: DefaultUrlBasicInfoService

    private val inputSuccessUrl = "http://inputsuccess.com"
    private val inputNoHttpSuccessUrl = "inputsuccess.com"
    private val inputFailUrl = "http://inputfail.com"
    private val redirectUrl = "http://redirect.com"
    private val resultTitle = "resultTitle"
    private val resultDescription = "resultDescription"
    private val resultUrl = "resultUrl"
    private val resultImage = "resultImage"
    private val resultSiteName = "resultSiteName"
    private val resultType = "resultType"

    @Test
    fun defaultUrlBasicInfoService_testGetLinkBasicInfoSuccess_shouldReturnValidResult() = runTest {

        defaultUrlBasicInfoService = DefaultUrlBasicInfoService(Dispatchers.Default, mockJsoupWrapper())

        val openGraphResult = defaultUrlBasicInfoService.getLinkBasicInfo(inputSuccessUrl)
        // 驗證結果
        assertEquals(resultImage, openGraphResult.image)
        assertEquals(resultDescription, openGraphResult.description)
        assertEquals(resultUrl, openGraphResult.url)
        assertEquals(resultTitle, openGraphResult.title)
        assertEquals(resultSiteName, openGraphResult.siteName)
        assertEquals(resultType, openGraphResult.type)
    }

    @Test
    fun defaultUrlBasicInfoService_testGetLinkBasicInfoNoHttpSuccess_shouldReturnValidResult() = runTest {

        defaultUrlBasicInfoService = DefaultUrlBasicInfoService(Dispatchers.Default, mockJsoupWrapper())

        val openGraphResult = defaultUrlBasicInfoService.getLinkBasicInfo(inputNoHttpSuccessUrl)
        // 驗證結果
        assertEquals(resultImage, openGraphResult.image)
        assertEquals(resultDescription, openGraphResult.description)
        assertEquals(resultUrl, openGraphResult.url)
        assertEquals(resultTitle, openGraphResult.title)
        assertEquals(resultSiteName, openGraphResult.siteName)
        assertEquals(resultType, openGraphResult.type)
    }

    @Test
    fun defaultUrlBasicInfoService_testGetLinkBasicInfoFail_shouldReturnFailResult() = runTest {

        defaultUrlBasicInfoService = DefaultUrlBasicInfoService(Dispatchers.Default, mockJsoupWrapper())

        val openGraphResult = defaultUrlBasicInfoService.getLinkBasicInfo(inputFailUrl)
        // 驗證結果
        assertNull(openGraphResult.image)
        assertNull(openGraphResult.description)
        assertNull(openGraphResult.url)
        assertNull(openGraphResult.title)
        assertNull(openGraphResult.siteName)
        assertNull(openGraphResult.type)
    }

    private fun mockJsoupWrapper(): JsoupWrapper {
        val successConnection = mock(Connection::class.java)
        val failConnection = mock(Connection::class.java)
        val response = mock(Response::class.java)
        val document = mock(Document::class.java)
        `when`(document.select(CSS_QUERY)).thenReturn(redirectInfoTag)
        `when`(document.select(DOC_SELECT_QUERY)).thenReturn(finalInfoTag)
        `when`(successConnection.get()).thenReturn(document)
        `when`(successConnection.ignoreContentType(true)).thenReturn(successConnection)
        `when`(successConnection.userAgent(AGENT)).thenReturn(successConnection)
        `when`(successConnection.referrer(REFERRER)).thenReturn(successConnection)
        `when`(successConnection.timeout(TIMEOUT)).thenReturn(successConnection)
        `when`(successConnection.followRedirects(true)).thenReturn(successConnection)
        `when`(response.parse()).thenReturn(document)
        `when`(successConnection.execute()).thenReturn(response)
        val jsoupWrapper = mock(JsoupWrapper::class.java)
        `when`(jsoupWrapper.getConnection(inputSuccessUrl)).thenReturn(successConnection)
        `when`(jsoupWrapper.getConnection(inputFailUrl)).thenReturn(failConnection)
        `when`(jsoupWrapper.getConnection(redirectUrl)).thenReturn(successConnection)
        return jsoupWrapper
    }

    private val finalInfoTag = Elements(
        listOf(
            mock(Element::class.java).apply {
                `when`(attr(PROPERTY)).thenReturn(OG_IMAGE)
                `when`(attr(OPEN_GRAPH_KEY)).thenReturn(resultImage)
            },
            mock(Element::class.java).apply {
                `when`(attr(PROPERTY)).thenReturn(OG_DESCRIPTION)
                `when`(attr(OPEN_GRAPH_KEY)).thenReturn(resultDescription)
            },
            mock(Element::class.java).apply {
                `when`(attr(PROPERTY)).thenReturn(OG_URL)
                `when`(attr(OPEN_GRAPH_KEY)).thenReturn(resultUrl)
            },
            mock(Element::class.java).apply {
                `when`(attr(PROPERTY)).thenReturn(OG_TITLE)
                `when`(attr(OPEN_GRAPH_KEY)).thenReturn(resultTitle)
            },
            mock(Element::class.java).apply {
                `when`(attr(PROPERTY)).thenReturn(OG_SITE_NAME)
                `when`(attr(OPEN_GRAPH_KEY)).thenReturn(resultSiteName)
            },
            mock(Element::class.java).apply {
                `when`(attr(PROPERTY)).thenReturn(DefaultUrlBasicInfoService.OG_TYPE)
                `when`(attr(OPEN_GRAPH_KEY)).thenReturn(resultType)
            }
        )
    )

    private val redirectInfoTag = Elements(
        listOf(
            mock(Element::class.java).apply {
                `when`(attr(DefaultUrlBasicInfoService.REDIRECT_URL_KEY)).thenReturn(redirectUrl)
            }
        )
    )
}

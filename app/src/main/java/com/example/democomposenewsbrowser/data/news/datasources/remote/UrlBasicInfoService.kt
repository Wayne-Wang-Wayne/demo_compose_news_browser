package com.example.democomposenewsbrowser.data.news.datasources.remote

import com.example.democomposenewsbrowser.di.IoDispatcher
import com.example.democomposenewsbrowser.util.DefaultJsoupWrapper
import com.example.democomposenewsbrowser.util.JsoupWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import javax.inject.Inject

interface UrlBasicInfoService {
    suspend fun getLinkBasicInfo(url: String): OpenGraphResult
}

class DefaultUrlBasicInfoService @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val jsoupWrapper: JsoupWrapper
) : UrlBasicInfoService {

    companion object {
        const val AGENT = "Mozilla"
        const val CSS_QUERY = "a"
        const val REDIRECT_URL_KEY = "href"
        const val REFERRER = "http://www.google.com"
        const val TIMEOUT = 10000
        const val DOC_SELECT_QUERY = "meta[property^=og:]"
        const val OPEN_GRAPH_KEY = "content"
        const val PROPERTY = "property"
        const val OG_IMAGE = "og:image"
        const val OG_DESCRIPTION = "og:description"
        const val OG_URL = "og:url"
        const val OG_TITLE = "og:title"
        const val OG_SITE_NAME = "og:site_name"
        const val OG_TYPE = "og:type"
    }


    override suspend fun getLinkBasicInfo(url: String): OpenGraphResult = withContext(dispatcher) {
        var mUrl = url
        if (!url.contains("http")) {
            mUrl = "http://$url"
        }

        val openGraphResult = OpenGraphResult()

        try {
            val document: Document =
                jsoupWrapper.getConnection(mUrl).get()

            val linkElement: Element = document.select(CSS_QUERY).first()!!
            val redirectUrl: String = linkElement.attr(REDIRECT_URL_KEY)
            openGraphResult.redirectUrl = redirectUrl
            val response = jsoupWrapper.getConnection(redirectUrl)
                .ignoreContentType(true)
                .userAgent(AGENT)
                .referrer(REFERRER)
                .timeout(TIMEOUT)
                .followRedirects(true)
                .execute()

            val doc = response.parse()

            val ogTags = doc.select(DOC_SELECT_QUERY)
            when {
                ogTags.size > 0 ->
                    ogTags.forEachIndexed { index, _ ->
                        val tag = ogTags[index]

                        when (tag.attr(PROPERTY)) {
                            OG_IMAGE -> {
                                openGraphResult.image = (tag.attr(OPEN_GRAPH_KEY))
                            }
                            OG_DESCRIPTION -> {
                                openGraphResult.description = (tag.attr(OPEN_GRAPH_KEY))
                            }
                            OG_URL -> {
                                openGraphResult.url = (tag.attr(OPEN_GRAPH_KEY))
                            }
                            OG_TITLE -> {
                                openGraphResult.title = (tag.attr(OPEN_GRAPH_KEY))
                            }
                            OG_SITE_NAME -> {
                                openGraphResult.siteName = (tag.attr(OPEN_GRAPH_KEY))
                            }
                            OG_TYPE -> {
                                openGraphResult.type = (tag.attr(OPEN_GRAPH_KEY))
                            }
                        }
                    }
            }
            openGraphResult
        } catch (e: Exception) {
            openGraphResult
        }
    }

}

data class OpenGraphResult(
    var redirectUrl: String? = null,
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var image: String? = null,
    var siteName: String? = null,
    var type: String? = null
)
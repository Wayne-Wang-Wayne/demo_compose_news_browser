package com.example.mvvmcomposenewsbrowser.data.news.datasources.remote

import com.example.mvvmcomposenewsbrowser.di.DefaultDispatcher
import com.example.mvvmcomposenewsbrowser.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

interface UrlBasicInfoService {
    suspend fun getLinkBasicInfo(url: String): OpenGraphResult
}

class DefaultUrlBasicInfoService @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : UrlBasicInfoService {

    companion object {
        private const val AGENT = "Mozilla"
        private const val REFERRER = "http://www.google.com"
        private const val TIMEOUT = 10000
        private const val DOC_SELECT_QUERY = "meta[property^=og:]"
        private const val OPEN_GRAPH_KEY = "content"
        private const val PROPERTY = "property"
        private const val OG_IMAGE = "og:image"
        private const val OG_DESCRIPTION = "og:description"
        private const val OG_URL = "og:url"
        private const val OG_TITLE = "og:title"
        private const val OG_SITE_NAME = "og:site_name"
        private const val OG_TYPE = "og:type"
    }


    override suspend fun getLinkBasicInfo(url: String): OpenGraphResult = withContext(dispatcher) {
        var mUrl = url
        if (!url.contains("http")) {
            mUrl = "http://$url"
        }

        val openGraphResult = OpenGraphResult()

        try {
            val document: Document =
                Jsoup.connect(mUrl).get()

            val linkElement: Element = document.select("a").first()!!
            val redirectUrl: String = linkElement.attr("href")
            openGraphResult.redirectUrl = redirectUrl
            val response = Jsoup.connect(redirectUrl)
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
package com.example.democomposenewsbrowser.util

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

interface JsoupWrapper {
    fun getConnection(url: String): Connection
}

// 為了要能unit test static method
class DefaultJsoupWrapper @Inject constructor() : JsoupWrapper {
    override fun getConnection(url: String): Connection = Jsoup.connect(url)
}
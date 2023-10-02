package com.example.shared_test.newsbrowser.network

import com.example.democomposenewsbrowser.data.news.datasources.remote.OpenGraphResult
import com.example.democomposenewsbrowser.data.news.datasources.remote.UrlBasicInfoService

class FakeUrlBasicInfoService : UrlBasicInfoService {

    override suspend fun getLinkBasicInfo(url: String): OpenGraphResult = fakeUrlBasicMapping[url] ?: OpenGraphResult()

}

val fakeUrlBasicMapping = mapOf(
    "url1" to OpenGraphResult(),
    "url2" to OpenGraphResult(
        redirectUrl = "redirectUrl2",
        title = "basicTitle2",
        description = "description2",
        url = "url2",
        image = "image2",
        siteName = "siteName2",
        type = "type2"
    )
)
package com.example.mvvmcomposenewsbrowser.data.news

import com.example.mvvmcomposenewsbrowser.data.news.datasources.local.LikedArticleDao
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsApiService
import com.example.mvvmcomposenewsbrowser.di.ApplicationScope
import com.example.mvvmcomposenewsbrowser.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val likedArticleDao: LikedArticleDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): NewsRepository {

    override fun getWhateverNews(): Flow<RepoLevelData> {
//        newsApiService.getWhateverNews().map {
//                if(it.isSuccessful && it.body() != null) {
//                    RepoLevelData(null, Status.Success)
//                } else {
//                    RepoLevelData(null, Status.ERROR(it.message()))
//                }
//            }.catch {
//                emit(RepoLevelData(null, Status.ERROR(it.message ?: "")))
//        }
        TODO("Not yet implemented")
    }

    override fun getSpecificNews(): Flow<RepoLevelData> {
        TODO("Not yet implemented")
    }

}
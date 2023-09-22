package com.example.mvvmcomposenewsbrowser.data.news.datasources.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedArticleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(localLikedArticle: LocalLikedArticle)

    @Query("SELECT EXISTS(SELECT * FROM liked_article WHERE title = :title)")
    fun isLiked(title: String): Boolean

    @Query("SELECT * from liked_article")
    fun getLikedArticle(): Flow<List<LocalLikedArticle>>

    @Delete
    suspend fun delete(localLikedArticle: LocalLikedArticle)

}
package com.example.democomposenewsbrowser.data.news.datasources.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedNewsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(localLikedNews: LocalLikedNews)

    @Query("SELECT EXISTS(SELECT * FROM liked_article WHERE url = :url)")
    fun isLiked(url: String): Boolean

    @Query("SELECT * from liked_article")
    fun getLikedNews(): Flow<List<LocalLikedNews>>

    @Delete
    suspend fun delete(localLikedNews: LocalLikedNews)
}
package com.prapti.lifememo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionDao {
    @Insert
    suspend fun insertEmotion(emotion: Emotion)

    @Query("SELECT * FROM Emotion ORDER BY timestamp DESC")
    fun getAllEmotions(): Flow<List<Emotion>>

    @Delete
    suspend fun delete(emotion: Emotion)

    @Query("DELETE FROM Emotion")
    suspend fun deleteAll()


}
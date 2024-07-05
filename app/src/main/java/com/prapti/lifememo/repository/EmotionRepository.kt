package com.prapti.lifememo.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.prapti.lifememo.database.Emotion
import com.prapti.lifememo.database.EmotionDao
import kotlinx.coroutines.flow.Flow

class EmotionRepository(private val emotionDao: EmotionDao) {
    val allEmotions: Flow<List<Emotion>> = emotionDao.getAllEmotions()

    suspend fun insert(emotion: Emotion) {
        emotionDao.insertEmotion(emotion)
    }
    suspend fun delete(emotion: Emotion) {
        emotionDao.delete(emotion)
    }

    suspend fun deleteAll() {
        emotionDao.deleteAll()
    }



}

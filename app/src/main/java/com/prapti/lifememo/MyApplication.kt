package com.prapti.lifememo

import android.app.Application
import com.prapti.lifememo.database.AppDatabase
import com.prapti.lifememo.repository.EmotionRepository
import com.prapti.lifememo.repository.JournalRepository

class MyApplication : Application() {
    lateinit var emotionRepository: EmotionRepository
    lateinit var journalRepository: JournalRepository
    override fun onCreate() {
        super.onCreate()
        emotionRepository = EmotionRepository(AppDatabase.getDatabase(this).emotionDao())
        journalRepository = JournalRepository(AppDatabase.getDatabase(this).journalDao())
    }
}
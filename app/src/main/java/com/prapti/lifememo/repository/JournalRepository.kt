package com.prapti.lifememo.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.prapti.lifememo.database.JournalDao
import com.prapti.lifememo.database.JournalEntry

class JournalRepository(private val journalDao: JournalDao) {

    suspend fun insert(entry: JournalEntry) = journalDao.insert(entry)


    suspend fun update(entry: JournalEntry) = journalDao.update(entry)


    suspend fun delete(entry: JournalEntry) = journalDao.delete(entry)


    suspend fun searchEntries(query: String): List<JournalEntry> = journalDao.searchEntries(query)


    suspend fun getAllEntries(): List<JournalEntry> = journalDao.getAllEntries()

    }

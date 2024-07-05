package com.prapti.lifememo.database
import androidx.room.*

@Dao
interface JournalDao {
    @Insert
    suspend fun insert(entry: JournalEntry)

    @Update
    suspend fun update(entry: JournalEntry)

    @Delete
    suspend fun delete(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries WHERE title LIKE :query OR dateTime LIKE :query")
    suspend fun searchEntries(query: String): List<JournalEntry>

    @Query("SELECT * FROM journal_entries")
    suspend fun getAllEntries(): List<JournalEntry>
}

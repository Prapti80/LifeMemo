package com.prapti.lifememo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prapti.lifememo.database.JournalEntry
import com.prapti.lifememo.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _entries = mutableStateListOf<JournalEntry>()
    val entries: List<JournalEntry> = _entries
    var searchQuery = mutableStateOf("")

    init {
        viewModelScope.launch {
            _entries.addAll(repository.getAllEntries())
        }
    }

    fun addEntry(title: String, content: String, color: Long, imageUris: String?) {
        val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val newEntry = JournalEntry(
            title = title,
            content = content,
            dateTime = currentDateTime,
            color = color,
            imageUris = imageUris
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(newEntry)
            _entries.add(newEntry)
        }
    }

    fun updateEntry(updatedEntry: JournalEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(updatedEntry)
            val index = _entries.indexOfFirst { it.id == updatedEntry.id }
            if (index != -1) {
                _entries[index] = updatedEntry
            }
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(entry)
            _entries.remove(entry)
        }
    }
    fun searchEntries(query: String) {
        viewModelScope.launch {
            _entries.clear()
            _entries.addAll(repository.searchEntries("%$query%"))
        }
    }

    fun filterEntries(query: String): List<JournalEntry> {
        return if (query.isEmpty()) {
            _entries
        } else {
            _entries.filter {
                it.title.contains(query, ignoreCase = true) || it.dateTime.contains(query, ignoreCase = true)
            }
        }
    }
}

package com.prapti.lifememo.viewmodel

import androidx.lifecycle.*
import com.prapti.lifememo.database.Emotion
import com.prapti.lifememo.repository.EmotionRepository
import kotlinx.coroutines.launch

class EmotionViewModel(private val repository: EmotionRepository) : ViewModel() {

    val allEmotions: LiveData<List<Emotion>> = repository.allEmotions.asLiveData()

    fun insert(emotion: Emotion) = viewModelScope.launch {
        repository.insert(emotion)
    }

    fun clearEmotions() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun getEmotionsCount(): LiveData<Map<String, Int>> {
        return allEmotions.map { emotions ->
            emotions.groupingBy { it.emotion }.eachCount()
        }
    }

    val emotionTimes: LiveData<Pair<Long?, Long?>> = allEmotions.map { emotions ->
        val sortedEmotions = emotions.sortedBy { it.timestamp }
        val startTime = sortedEmotions.firstOrNull()?.timestamp
        val endTime = sortedEmotions.lastOrNull()?.timestamp
        Pair(startTime, endTime)
    }
}

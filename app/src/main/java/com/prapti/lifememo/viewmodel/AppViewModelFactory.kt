package com.prapti.lifememo.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.prapti.lifememo.MyApplication


object AppViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            EmotionViewModel(myApplication().emotionRepository)
        }
        initializer {
            JournalViewModel(myApplication().journalRepository)
        }

    }
}

fun CreationExtras.myApplication(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
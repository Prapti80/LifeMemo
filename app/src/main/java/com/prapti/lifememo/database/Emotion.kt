package com.prapti.lifememo.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Emotion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val emotion: String,
    val timestamp: Long
)
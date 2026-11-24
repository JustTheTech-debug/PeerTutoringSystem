package com.example.peertutoringsystem.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,
    val offerId: Int,
    val requestId: Int,
    val dateTime: Long,      // milliseconds
    val mode: String,
    val locationOrLink: String,
    val status: String = "SCHEDULED"
)
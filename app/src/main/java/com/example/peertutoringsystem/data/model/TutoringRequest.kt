package com.example.peertutoringsystem.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class TutoringRequest(
    @PrimaryKey(autoGenerate = true) val requestId: Int = 0,
    val studentId: Int,
    val subject: String,
    val level: String,
    val mode: String,        // "ONLINE" or "IN_PERSON"
    val description: String,
    val status: String = "OPEN"
)
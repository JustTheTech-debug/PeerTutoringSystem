package com.example.peertutoringsystem.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offers")
data class TutorOffer(
    @PrimaryKey(autoGenerate = true) val offerId: Int = 0,
    val requestId: Int,
    val tutorId: Int,
    val fee: Int,
    val message: String,
    val status: String = "PENDING"
)
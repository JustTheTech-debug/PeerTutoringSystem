package com.example.peertutoringsystem.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val name: String,
    val phone: String,
    val password: String,
    val role: String,       // for "STUDENT" or "TUTOR"
    val level: String,
    val subjects: String    // for tutors, e.g. "Mathematics,Physics"
)
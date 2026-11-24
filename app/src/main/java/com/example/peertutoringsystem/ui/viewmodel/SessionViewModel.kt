package com.example.peertutoringsystem.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.peertutoringsystem.data.local.AppDatabase
import com.example.peertutoringsystem.data.model.Session
import com.example.peertutoringsystem.data.repository.SessionRepository
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    // Build the data layer once: database -> DAOs -> repository
    private val db = AppDatabase.getDatabase(application)
    private val repository = SessionRepository(db.sessionDao(), db.requestDao())

    // The Sessions List screen passes in the current time
    fun upcoming(now: Long): LiveData<List<Session>> = repository.upcoming(now)

    fun past(now: Long): LiveData<List<Session>> = repository.past(now)

    fun loadSession(id: Int): LiveData<Session?> = liveData {
        emit(repository.getById(id))
    }

    // Returns an error message, or null if the session was saved
    fun scheduleSession(
        offerId: Int, requestId: Int,
        dateTime: Long, mode: String, locationOrLink: String
    ): String? {
        val error = checkInput(dateTime, mode, locationOrLink)
        if (error != null) return error

        val session = Session(
            offerId = offerId, requestId = requestId,
            dateTime = dateTime, mode = mode,
            locationOrLink = locationOrLink.trim()
        )
        viewModelScope.launch { repository.schedule(session) }
        return null
    }

    // Returns an error message, or null if the session was rescheduled
    fun rescheduleSession(sessionId: Int, dateTime: Long, locationOrLink: String): String? {
        if (dateTime <= System.currentTimeMillis()) return "Date and time must be in the future"
        if (locationOrLink.isBlank()) return "Enter a link or a location"

        viewModelScope.launch {
            repository.reschedule(sessionId, dateTime, locationOrLink.trim())
        }
        return null
    }

    fun cancelSession(sessionId: Int) {
        viewModelScope.launch { repository.cancel(sessionId) }
    }

    // Sets the session and its request to COMPLETED
    fun markCompleted(session: Session) {
        viewModelScope.launch { repository.markCompleted(session) }
    }

    private fun checkInput(dateTime: Long, mode: String, locationOrLink: String): String? {
        if (dateTime <= System.currentTimeMillis()) return "Date and time must be in the future"
        if (mode != "ONLINE" && mode != "IN_PERSON") return "Choose online or in-person"
        if (locationOrLink.isBlank()) return "Enter a link or a location"
        return null
    }
}
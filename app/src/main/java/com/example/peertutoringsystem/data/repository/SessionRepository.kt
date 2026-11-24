package com.example.peertutoringsystem.data.repository

import androidx.lifecycle.LiveData
import com.example.peertutoringsystem.data.local.RequestDao
import com.example.peertutoringsystem.data.local.SessionDao
import com.example.peertutoringsystem.data.model.Session

// The single door into the data layer for sessions
class SessionRepository(
    private val dao: SessionDao,
    private val requestDao: RequestDao   // needed so "mark completed" can also complete the request
) {

    fun upcoming(now: Long): LiveData<List<Session>> = dao.getUpcoming(now)

    fun past(now: Long): LiveData<List<Session>> = dao.getPast(now)

    suspend fun getById(id: Int) = dao.getById(id)

    suspend fun schedule(session: Session) = dao.insert(session)

    suspend fun cancel(sessionId: Int) = dao.updateStatus(sessionId, "CANCELLED")

    suspend fun reschedule(sessionId: Int, dateTime: Long, locationOrLink: String) =
        dao.reschedule(sessionId, dateTime, locationOrLink)

    // Sets the session AND its request to COMPLETED
    suspend fun markCompleted(session: Session) {
        dao.updateStatus(session.sessionId, "COMPLETED")
        val request = requestDao.getRequestById(session.requestId)
        if (request != null) {
            requestDao.updateRequest(request.copy(status = "COMPLETED"))
        }
    }
}
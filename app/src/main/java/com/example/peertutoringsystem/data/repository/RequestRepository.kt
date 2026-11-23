package com.example.peertutoringsystem.data.repository


import androidx.lifecycle.LiveData
import com.example.peertutoringsystem.data.local.RequestDao
import com.example.peertutoringsystem.data.model.TutoringRequest

// The single door into the data layer: the ViewModel only talks to this class
class RequestRepository(private val dao: RequestDao) {

    val openRequests: LiveData<List<TutoringRequest>> = dao.getOpenRequests()

    fun search(text: String) = dao.searchOpenBySubject(text)

    fun requestsByStudent(studentId: Int) = dao.getRequestsByStudent(studentId)

    suspend fun getById(id: Int) = dao.getRequestById(id)

    // requestId 0 means "not saved yet", so insert; otherwise update
    suspend fun save(request: TutoringRequest) {
        if (request.requestId == 0) dao.insertRequest(request) else dao.updateRequest(request)
    }

    suspend fun delete(request: TutoringRequest) = dao.deleteRequest(request)
}
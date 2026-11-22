package com.example.peertutoringsystem.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.peertutoringsystem.data.local.AppDatabase
import com.example.peertutoringsystem.data.model.TutoringRequest
import com.example.peertutoringsystem.data.repository.RequestRepository
import kotlinx.coroutines.launch

// AndroidViewModel receives the Application, which is safe to keep
class RequestViewModel(application: Application) : AndroidViewModel(application) {

    // Build the data layer once: database -> DAO -> repository
    private val repository =
        RequestRepository(AppDatabase.getDatabase(application).requestDao())

    private val searchQuery = MutableLiveData<String>("")

    // Open requests: all of them, or only those matching the search text
    val openRequests: LiveData<List<TutoringRequest>> = searchQuery.switchMap { text ->
        if (text.isBlank()) repository.openRequests else repository.search(text.trim())
    }

    fun setSearch(text: String) { searchQuery.value = text }

    fun requestsForStudent(studentId: Int) = repository.requestsByStudent(studentId)

    fun loadRequest(id: Int): LiveData<TutoringRequest?> = liveData {
        emit(repository.getById(id))
    }

    // Returns an error message, or null if the request was saved.
    // existing = null means a NEW request
    fun saveRequest(
        existing: TutoringRequest?, studentId: Int,
        subject: String, level: String, mode: String, description: String
    ): String? {
        val cleanSubject = subject.trim()
        if (cleanSubject.isEmpty()) return "Subject cannot be blank"
        if (mode != "ONLINE" && mode != "IN_PERSON") return "Choose online or in-person"

        val request = existing?.copy(
            subject = cleanSubject, level = level,
            mode = mode, description = description.trim()
        ) ?: TutoringRequest(
            studentId = studentId, subject = cleanSubject, level = level,
            mode = mode, description = description.trim()
        )
        viewModelScope.launch { repository.save(request) }   // runs in the background
        return null
    }

    fun deleteRequest(request: TutoringRequest) {
        viewModelScope.launch { repository.delete(request) }
    }
}
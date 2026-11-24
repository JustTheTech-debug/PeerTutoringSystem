package com.example.peertutoringsystem.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.peertutoringsystem.data.local.AppDatabase
import com.example.peertutoringsystem.data.model.User
import com.example.peertutoringsystem.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun registerUser(user: User, onSuccess: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // Changed from repository.register to repository.insertUser
            val id = repository.insertUser(user)
            onSuccess(id) // Return the new user ID to save in SessionManager
        }
    }

    fun loginUser(phone: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.login(phone, password)
            onResult(user)
        }
    }
}
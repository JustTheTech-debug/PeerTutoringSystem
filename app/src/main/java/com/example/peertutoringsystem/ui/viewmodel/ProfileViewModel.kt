package com.example.peertutoringsystem.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.peertutoringsystem.data.local.AppDatabase
import com.example.peertutoringsystem.data.model.User
import com.example.peertutoringsystem.data.repository.SyncRepository
import com.example.peertutoringsystem.data.repository.SyncResult
import com.example.peertutoringsystem.data.repository.UserRepository
import com.example.peertutoringsystem.util.Validator
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val syncRepository: SyncRepository

    val currentUser = MutableLiveData<User?>()

    val nameError = MutableLiveData<String?>()
    val phoneError = MutableLiveData<String?>()
    val levelError = MutableLiveData<String?>()
    val subjectsError = MutableLiveData<String?>()

    val statusMessage = MutableLiveData<String?>()
    val isSaving = MutableLiveData<Boolean>(false)

    init {
        val db = AppDatabase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
        syncRepository = SyncRepository(application)
    }

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            val user = userRepository.getUserSync(userId)
            if (user == null) {
                // Create a sample/placeholder user for initial testing if none exists in Room
                val sampleUser = User(
                    userId = userId,
                    name = "John Doe",
                    phone = "0712345678",
                    password = "password123",
                    role = "TUTOR",
                    level = "Undergraduate",
                    subjects = "Mathematics, Physics"
                )
                userRepository.insertUser(sampleUser)
                currentUser.postValue(sampleUser)
            } else {
                currentUser.postValue(user)
            }
        }
    }

    fun saveProfile(name: String, phone: String, level: String, subjects: String) {
        val user = currentUser.value ?: return

        // Clear previous errors
        nameError.value = null
        phoneError.value = null
        levelError.value = null
        subjectsError.value = null

        var isValid = true

        if (!Validator.isNotBlank(name)) {
            nameError.value = "Name cannot be blank"
            isValid = false
        }

        if (!Validator.isValidPhone(phone)) {
            phoneError.value = "Phone must be exactly 10 digits"
            isValid = false
        }

        if (!Validator.isValidLevel(level)) {
            levelError.value = "Level cannot be blank"
            isValid = false
        }

        if (user.role == "TUTOR" && !Validator.isNotBlank(subjects)) {
            subjectsError.value = "Subjects cannot be blank for tutors"
            isValid = false
        }

        if (!isValid) return

        isSaving.value = true

        val updatedUser = user.copy(
            name = name.trim(),
            phone = phone.trim(),
            level = level.trim(),
            subjects = if (user.role == "TUTOR") subjects.trim() else user.subjects
        )

        viewModelScope.launch {
            try {
                userRepository.updateUser(updatedUser)
                currentUser.value = updatedUser

                // Online sync in coroutine
                val syncResult = syncRepository.syncProfileOnline(
                    updatedUser.userId,
                    updatedUser.name,
                    updatedUser.phone,
                    updatedUser.level
                )

                when (syncResult) {
                    is SyncResult.Success -> statusMessage.value = "Profile updated and ${syncResult.message}"
                    is SyncResult.Offline -> statusMessage.value = "Profile updated locally. (${syncResult.message})"
                    is SyncResult.Error -> statusMessage.value = "Profile updated locally. ${syncResult.message}"
                }
            } catch (e: Exception) {
                statusMessage.value = "Failed to save profile: ${e.localizedMessage}"
            } finally {
                isSaving.value = false
            }
        }
    }
}

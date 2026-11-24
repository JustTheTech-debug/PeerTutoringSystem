package com.example.peertutoringsystem.data.repository

import androidx.lifecycle.LiveData
import com.example.peertutoringsystem.data.local.UserDao
import com.example.peertutoringsystem.data.model.User

class UserRepository(private val userDao: UserDao) {

    fun getUser(userId: Int): LiveData<User?> = userDao.getUserById(userId)

    suspend fun getUserSync(userId: Int): User? = userDao.getUserByIdSync(userId)

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    //FOR THE LOGIN SCREEN ---
    suspend fun login(phone: String, password: String): User? {
        return userDao.login(phone, password)
    }
}
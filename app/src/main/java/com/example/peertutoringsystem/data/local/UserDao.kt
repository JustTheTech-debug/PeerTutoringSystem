package com.example.peertutoringsystem.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.peertutoringsystem.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE userId = :id")
    fun getUserById(id: Int): LiveData<User?>

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserByIdSync(id: Int): User?

    // --- FOR THE LOGIN SCREEN ---
    @Query("SELECT * FROM users WHERE phone = :phone AND password = :password LIMIT 1")
    suspend fun login(phone: String, password: String): User?
}
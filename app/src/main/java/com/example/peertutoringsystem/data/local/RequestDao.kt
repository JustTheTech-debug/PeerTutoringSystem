package com.example.peertutoringsystem.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.peertutoringsystem.data.model.TutoringRequest

@Dao
interface RequestDao {

    // suspend = runs in the background without freezing the screen
    @Insert
    suspend fun insertRequest(request: TutoringRequest): Long

    @Update
    suspend fun updateRequest(request: TutoringRequest)

    @Delete
    suspend fun deleteRequest(request: TutoringRequest)

    @Query("SELECT * FROM requests WHERE requestId = :id")
    suspend fun getRequestById(id: Int): TutoringRequest?

    // Tutors: all open requests, newest first
    // LiveData: the screen updates by itself when the table changes
    @Query("SELECT * FROM requests WHERE status = 'OPEN' ORDER BY requestId DESC")
    fun getOpenRequests(): LiveData<List<TutoringRequest>>

    // Students: their own requests
    @Query("SELECT * FROM requests WHERE studentId = :studentId ORDER BY requestId DESC")
    fun getRequestsByStudent(studentId: Int): LiveData<List<TutoringRequest>>

    // Search open requests by subject
    @Query("SELECT * FROM requests WHERE status = 'OPEN' AND subject LIKE '%' || :text || '%' ORDER BY requestId DESC")
    fun searchOpenBySubject(text: String): LiveData<List<TutoringRequest>>
}
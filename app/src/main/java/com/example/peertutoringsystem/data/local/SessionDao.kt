package com.example.peertutoringsystem.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.peertutoringsystem.data.model.Session

@Dao
interface SessionDao {

    // ✅ READY TO USE
    @Insert
    suspend fun insert(session: Session): Long

    @Update
    suspend fun update(session: Session)

    @Query("SELECT * FROM sessions WHERE sessionId = :id")
    suspend fun getById(id: Int): Session?

    @Query("UPDATE sessions SET status = :status WHERE sessionId = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE sessions SET dateTime = :dateTime, locationOrLink = :locationOrLink WHERE sessionId = :id")
    suspend fun reschedule(id: Int, dateTime: Long, locationOrLink: String)

    @Query("""
        SELECT * FROM sessions
        WHERE status = 'SCHEDULED' AND dateTime >= :now
        ORDER BY dateTime ASC
    """)
    fun getUpcoming(now: Long): LiveData<List<Session>>

    @Query("""
        SELECT * FROM sessions
        WHERE status != 'SCHEDULED' OR dateTime < :now
        ORDER BY dateTime DESC
    """)
    fun getPast(now: Long): LiveData<List<Session>>
}
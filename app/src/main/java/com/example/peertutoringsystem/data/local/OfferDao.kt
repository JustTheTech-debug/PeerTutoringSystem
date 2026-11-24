package com.example.peertutoringsystem.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.peertutoringsystem.data.model.TutorOffer

@Dao
interface OfferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: TutorOffer): Long

    @Update
    suspend fun updateOffer(offer: TutorOffer)

    @Query("SELECT * FROM offers WHERE tutorId = :tutorId ORDER BY offerId DESC")
    fun getOffersByTutor(tutorId: Int): LiveData<List<TutorOffer>>

    @Query("SELECT * FROM offers WHERE requestId = :requestId")
    fun getOffersForRequest(requestId: Int): LiveData<List<TutorOffer>>

    @Query("SELECT * FROM offers WHERE offerId = :offerId")
    suspend fun getOfferById(offerId: Int): TutorOffer?
    @Query("SELECT * FROM offers WHERE requestId = :requestId AND tutorId = :tutorId LIMIT 1")
    suspend fun findExistingOffer(requestId: Int, tutorId: Int): TutorOffer?

    @Query("UPDATE offers SET status = :status WHERE offerId = :offerId")
    suspend fun updateStatus(offerId: Int, status: String)
}

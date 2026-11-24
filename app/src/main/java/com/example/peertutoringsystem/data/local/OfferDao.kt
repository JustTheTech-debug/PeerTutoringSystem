package com.example.peertutoringsystem.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.peertutoringsystem.data.model.TutorOffer

@Dao
interface OfferDao {

    // Save a new offer. Returns the new offerId
    @Insert
    suspend fun insertOffer(offer: TutorOffer): Long

    // Edit an offer (Member 4 uses this in My Offers)
    @Update
    suspend fun updateOffer(offer: TutorOffer)

    // Offers for one request (Offers for a Request screen)
    @Query("SELECT * FROM offers WHERE requestId = :requestId")
    fun getOffersForRequest(requestId: Int): LiveData<List<TutorOffer>>

    // Offers made by one tutor (Member 4's My Offers screen)
    @Query("SELECT * FROM offers WHERE tutorId = :tutorId")
    fun getOffersByTutor(tutorId: Int): LiveData<List<TutorOffer>>

    // One offer by id
    @Query("SELECT * FROM offers WHERE offerId = :offerId LIMIT 1")
    suspend fun getOfferById(offerId: Int): TutorOffer?

    // Rule "once per request": does this tutor already have an active offer here?
    @Query("SELECT * FROM offers WHERE requestId = :requestId AND tutorId = :tutorId AND status != 'WITHDRAWN' LIMIT 1")
    suspend fun findExistingOffer(requestId: Int, tutorId: Int): TutorOffer?

    // Change only the status (ACCEPTED or WITHDRAWN)
    @Query("UPDATE offers SET status = :status WHERE offerId = :offerId")
    suspend fun updateStatus(offerId: Int, status: String)
}
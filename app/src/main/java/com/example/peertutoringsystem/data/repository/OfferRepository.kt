package com.example.peertutoringsystem.data.repository

import androidx.lifecycle.LiveData
import com.example.peertutoringsystem.data.local.OfferDao
import com.example.peertutoringsystem.data.model.TutorOffer

class OfferRepository(private val offerDao: OfferDao) {

    fun getOffersByTutor(tutorId: Int): LiveData<List<TutorOffer>> =
        offerDao.getOffersByTutor(tutorId)

    suspend fun updateOffer(offer: TutorOffer) {
        offerDao.updateOffer(offer)
    }

    suspend fun insertOffer(offer: TutorOffer): Long {
        return offerDao.insertOffer(offer)
    }

    suspend fun withdrawOffer(offer: TutorOffer) {
        val updated = offer.copy(status = "WITHDRAWN")
        offerDao.updateOffer(updated)
    }

    suspend fun getOfferById(offerId: Int): TutorOffer? {
        return offerDao.getOfferById(offerId)
    }
}

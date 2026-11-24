package com.example.peertutoringsystem.data.repository

import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.example.peertutoringsystem.data.local.AppDatabase
import com.example.peertutoringsystem.data.model.TutorOffer
import com.example.peertutoringsystem.util.Validator

// The single door into offers. Screens/ViewModels never touch the DAOs directly
class OfferRepository(private val db: AppDatabase) {

    private val offerDao = db.offerDao()
    private val requestDao = db.requestDao()

    fun getOffersForRequest(requestId: Int): LiveData<List<TutorOffer>> =
        offerDao.getOffersForRequest(requestId)
import com.example.peertutoringsystem.data.local.OfferDao
import com.example.peertutoringsystem.data.model.TutorOffer

class OfferRepository(private val offerDao: OfferDao) {

    fun getOffersByTutor(tutorId: Int): LiveData<List<TutorOffer>> =
        offerDao.getOffersByTutor(tutorId)

    // Returns null if saved, or an error message to show the user
    suspend fun submitOffer(
        requestId: Int,
        tutorId: Int,
        tutorSubjects: String,
        feeText: String,
        message: String
    ): String? {
        val fee = feeText.trim().toIntOrNull()
        if (fee == null || fee <= 0) return "Enter a whole number above 0 for the fee"
        if (!Validator.isNotBlank(message)) return "Message cannot be blank"

        val request = requestDao.getRequestById(requestId) ?: return "Request not found"
        if (request.status != "OPEN") return "This request is no longer open"
        if (!teachesSubject(tutorSubjects, request.subject)) return "You do not teach ${request.subject}"
        if (offerDao.findExistingOffer(requestId, tutorId) != null) return "You already sent an offer for this request"

        offerDao.insertOffer(
            TutorOffer(requestId = requestId, tutorId = tutorId, fee = fee, message = message.trim())
        )
        return null
    }

    // Accept: offer -> ACCEPTED and request -> MATCHED, together or not at all
    suspend fun acceptOffer(offerId: Int): Boolean {
        return db.withTransaction {
            val offer = offerDao.getOfferById(offerId) ?: return@withTransaction false
            if (offer.status != "PENDING") return@withTransaction false
            val request = requestDao.getRequestById(offer.requestId) ?: return@withTransaction false
            if (request.status != "OPEN") return@withTransaction false

            offerDao.updateStatus(offerId, "ACCEPTED")
            requestDao.updateRequest(request.copy(status = "MATCHED"))
            true
        }
    }

    // For Member 4 (My Offers): accepted offers cannot be edited
    suspend fun editOffer(offer: TutorOffer, newFeeText: String, newMessage: String): String? {
        if (offer.status == "ACCEPTED") return "Accepted offers cannot be edited"
        val fee = newFeeText.trim().toIntOrNull()
        if (fee == null || fee <= 0) return "Enter a whole number above 0 for the fee"
        if (!Validator.isNotBlank(newMessage)) return "Message cannot be blank"
        offerDao.updateOffer(offer.copy(fee = fee, message = newMessage.trim()))
        return null
    }

    // For Member 4 (My Offers)
    suspend fun withdrawOffer(offerId: Int): Boolean {
        val offer = offerDao.getOfferById(offerId) ?: return false
        if (offer.status == "ACCEPTED") return false
        offerDao.updateStatus(offerId, "WITHDRAWN")
        return true
    }

    // "Mathematics,Physics" -> does it contain the request's subject?
    private fun teachesSubject(tutorSubjects: String, requestSubject: String): Boolean =
        tutorSubjects.split(",").map { it.trim() }
            .any { it.equals(requestSubject.trim(), ignoreCase = true) }
}
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

package com.example.peertutoringsystem.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.peertutoringsystem.data.local.AppDatabase
import com.example.peertutoringsystem.data.model.TutorOffer
import com.example.peertutoringsystem.data.repository.OfferRepository
import com.example.peertutoringsystem.data.repository.SyncRepository
import com.example.peertutoringsystem.data.repository.SyncResult
import com.example.peertutoringsystem.util.Validator
import kotlinx.coroutines.launch

class OffersViewModel(application: Application) : AndroidViewModel(application) {

    private val offerRepository: OfferRepository
    private val syncRepository: SyncRepository

    private val tutorIdLiveData = MutableLiveData<Int>()

    val myOffers: LiveData<List<TutorOffer>> = tutorIdLiveData.switchMap { tutorId ->
        offerRepository.getOffersByTutor(tutorId)
    }

    val statusMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>(false)

    init {
        val db = AppDatabase.getDatabase(application)
        offerRepository = OfferRepository(db.offerDao())
        syncRepository = SyncRepository(application)
    }

    fun setTutorId(tutorId: Int) {
        if (tutorIdLiveData.value != tutorId) {
            tutorIdLiveData.value = tutorId
        }
    }

    fun editOffer(offer: TutorOffer, newFeeString: String, newMessage: String) {
        if (offer.status == "ACCEPTED") {
            statusMessage.value = "Accepted offers cannot be edited."
            return
        }

        if (!Validator.isValidFee(newFeeString)) {
            statusMessage.value = "Fee must be a valid number above 0."
            return
        }

        if (!Validator.isNotBlank(newMessage)) {
            statusMessage.value = "Message cannot be blank."
            return
        }

        val newFee = newFeeString.trim().toIntOrNull() ?: 0

        val updatedOffer = offer.copy(
            fee = newFee,
            message = newMessage.trim()
        )

        isLoading.value = true

        viewModelScope.launch {
            try {
                offerRepository.updateOffer(updatedOffer)

                val syncResult = syncRepository.syncOfferOnline(
                    updatedOffer.offerId,
                    updatedOffer.fee,
                    updatedOffer.message
                )

                when (syncResult) {
                    is SyncResult.Success -> statusMessage.value = "Offer updated and synced online."
                    is SyncResult.Offline -> statusMessage.value = "Offer updated locally (${syncResult.message})"
                    is SyncResult.Error -> statusMessage.value = "Offer updated locally. ${syncResult.message}"
                }
            } catch (e: Exception) {
                statusMessage.value = "Error updating offer: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun withdrawOffer(offer: TutorOffer) {
        if (offer.status == "ACCEPTED") {
            statusMessage.value = "Accepted offers cannot be withdrawn."
            return
        }

        isLoading.value = true

        viewModelScope.launch {
            try {
                offerRepository.withdrawOffer(offer)

                val syncResult = syncRepository.syncOfferOnline(
                    offer.offerId,
                    offer.fee,
                    "WITHDRAWN"
                )

                when (syncResult) {
                    is SyncResult.Success -> statusMessage.value = "Offer withdrawn successfully."
                    is SyncResult.Offline -> statusMessage.value = "Offer withdrawn locally (${syncResult.message})"
                    is SyncResult.Error -> statusMessage.value = "Offer withdrawn locally. ${syncResult.message}"
                }
            } catch (e: Exception) {
                statusMessage.value = "Error withdrawing offer: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}

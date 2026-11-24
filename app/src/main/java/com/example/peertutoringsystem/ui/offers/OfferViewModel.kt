package com.example.peertutoringsystem.ui.offers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.peertutoringsystem.data.local.AppDatabase
import com.example.peertutoringsystem.data.model.TutorOffer
import com.example.peertutoringsystem.data.repository.OfferRepository
import kotlinx.coroutines.launch

class OfferViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OfferRepository(AppDatabase.getDatabase(application))

    fun offersForRequest(requestId: Int): LiveData<List<TutorOffer>> =
        repository.getOffersForRequest(requestId)

    fun offersByTutor(tutorId: Int): LiveData<List<TutorOffer>> =
        repository.getOffersByTutor(tutorId)

    // onResult gets null when saved, or an error message for the screen to show
    fun submitOffer(
        requestId: Int,
        tutorId: Int,
        feeText: String,
        message: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            onResult(repository.submitOffer(requestId, tutorId, feeText, message))
        }
    }

    fun acceptOffer(offerId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.acceptOffer(offerId)) }
    }

    fun editOffer(offer: TutorOffer, feeText: String, message: String, onResult: (String?) -> Unit) {
        viewModelScope.launch { onResult(repository.editOffer(offer, feeText, message)) }
    }

    fun withdrawOffer(offerId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.withdrawOffer(offerId)) }
    }
}
package com.example.peertutoringsystem.ui.offers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peertutoringsystem.data.model.TutorOffer
import com.example.peertutoringsystem.data.repository.OfferRepository
import kotlinx.coroutines.launch

class OfferViewModel(private val repo: OfferRepository) : ViewModel() {

    fun offersForRequest(requestId: Int): LiveData<List<TutorOffer>> =
        repo.getOffersForRequest(requestId)

    fun submitOffer(
        requestId: Int,
        tutorId: Int,
        tutorSubjects: String,
        feeText: String,
        message: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            val error = repo.submitOffer(requestId, tutorId, tutorSubjects, feeText, message)
            onResult(error)
        }
    }

    fun acceptOffer(offerId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = repo.acceptOffer(offerId)
            onResult(ok)
        }
    }
}
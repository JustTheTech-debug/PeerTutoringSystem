package com.example.peertutoringsystem.ui.offers

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peertutoringsystem.R

class OffersForRequestFragment : Fragment(R.layout.fragment_offers_request) {

    private val viewModel: OfferViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestId = arguments?.getInt("requestId", -1) ?: -1
        val recycler = view.findViewById<RecyclerView>(R.id.offersRecycler)
        val emptyText = view.findViewById<TextView>(R.id.emptyText)

        val adapter = OfferAdapter { offer ->
            viewModel.acceptOffer(offer.offerId) { success ->
                if (!isAdded) return@acceptOffer
                if (success) {
                    // Member 5 reads these two keys in Schedule Session
                    val args = bundleOf("requestId" to requestId, "offerId" to offer.offerId)
                    findNavController().navigate(R.id.action_offersForRequest_to_scheduleSession, args)
                } else {
                    Toast.makeText(requireContext(), "Could not accept this offer", Toast.LENGTH_SHORT).show()
                }
            }
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.offersForRequest(requestId).observe(viewLifecycleOwner) { offers ->
            adapter.submitList(offers)
            emptyText.visibility = if (offers.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
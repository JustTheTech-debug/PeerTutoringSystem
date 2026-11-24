package com.example.peertutoringsystem.ui.offers

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.data.model.TutorOffer
import com.example.peertutoringsystem.ui.viewmodel.OffersViewModel
import com.example.peertutoringsystem.util.SessionManager

class MyOffersFragment : Fragment() {

    private val viewModel: OffersViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MyOffersAdapter

    private lateinit var rvOffers: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        rvOffers = view.findViewById(R.id.rvMyOffers)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        progressBar = view.findViewById(R.id.progressBar)

        adapter = MyOffersAdapter(
            onEditClick = { offer -> showEditDialog(offer) },
            onWithdrawClick = { offer -> showWithdrawConfirmation(offer) }
        )

        rvOffers.layoutManager = LinearLayoutManager(requireContext())
        rvOffers.adapter = adapter

        val tutorId = sessionManager.getUserId()
        viewModel.setTutorId(tutorId)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.myOffers.observe(viewLifecycleOwner) { offers ->
            if (offers.isNullOrEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvOffers.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvOffers.visibility = View.VISIBLE
                adapter.submitList(offers)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditDialog(offer: TutorOffer) {
        if (offer.status == "ACCEPTED") {
            Toast.makeText(requireContext(), "Accepted offers cannot be edited.", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_offer, null)
        val etFee = dialogView.findViewById<EditText>(R.id.etDialogFee)
        val etMessage = dialogView.findViewById<EditText>(R.id.etDialogMessage)

        etFee.setText(offer.fee.toString())
        etMessage.setText(offer.message)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Offer")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val feeStr = etFee.text.toString()
                val msgStr = etMessage.text.toString()
                viewModel.editOffer(offer, feeStr, msgStr)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showWithdrawConfirmation(offer: TutorOffer) {
        if (offer.status == "ACCEPTED") {
            Toast.makeText(requireContext(), "Accepted offers cannot be withdrawn.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Withdraw Offer")
            .setMessage("Are you sure you want to withdraw this offer?")
            .setPositiveButton("Withdraw") { _, _ ->
                viewModel.withdrawOffer(offer)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

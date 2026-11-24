package com.example.peertutoringsystem.ui.offers

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.util.SessionManager

class RespondFragment : Fragment(R.layout.fragment_respond) {

    private val viewModel: OfferFlowViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestId = arguments?.getInt("requestId", -1) ?: -1
        val feeEdit = view.findViewById<EditText>(R.id.feeEditText)
        val messageEdit = view.findViewById<EditText>(R.id.messageEditText)

        view.findViewById<Button>(R.id.sendOfferButton).setOnClickListener {
            val tutorId = SessionManager(requireContext()).getUserId()

            viewModel.submitOffer(
                requestId, tutorId,
                feeEdit.text.toString(), messageEdit.text.toString()
            ) { error ->
                if (!isAdded) return@submitOffer
                if (error == null) {
                    Toast.makeText(requireContext(), "Offer sent", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
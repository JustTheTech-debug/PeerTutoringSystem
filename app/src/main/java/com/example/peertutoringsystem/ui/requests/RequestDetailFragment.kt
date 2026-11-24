package com.example.peertutoringsystem.ui.requests

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.peertutoringsystem.R

class RequestDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_detail, container, false)

        val tvRequestInfo = view.findViewById<TextView>(R.id.tvRequestInfo)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnRespond = view.findViewById<Button>(R.id.btnRespond)

        // Dummy data for the request
        tvRequestInfo.text = "Math Tutoring\nLevel: Form 4\nMode: ONLINE\nDescription: Need help with calculus."

        btnEdit.setOnClickListener {
            Toast.makeText(context, "Edit Request clicked", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            Toast.makeText(context, "Cancel Request clicked", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this request?")
                .setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }

        btnRespond.setOnClickListener {
            Toast.makeText(context, "Respond button clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
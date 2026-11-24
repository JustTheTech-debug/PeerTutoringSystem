package com.example.peertutoringsystem.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.peertutoringsystem.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val tvMyRequests = view.findViewById<TextView>(R.id.tvMyRequests)
        val tvOpenRequests = view.findViewById<TextView>(R.id.tvOpenRequests)
        val tvUpcomingSessions = view.findViewById<TextView>(R.id.tvUpcomingSessions)

        // Dummy data for the dashboard
        tvWelcome.text = "Welcome back!"
        tvMyRequests.text = "My Requests: 5"
        tvOpenRequests.text = "Open Requests: 12"
        tvUpcomingSessions.text = "Upcoming Sessions: 2"

        return view
    }
}
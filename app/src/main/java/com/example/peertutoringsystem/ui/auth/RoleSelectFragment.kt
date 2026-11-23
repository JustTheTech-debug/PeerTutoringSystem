package com.example.peertutoringsystem.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
class RoleSelectFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (we will make layouts later)
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false).apply {
            findViewById<TextView>(android.R.id.text1).text = "Role Select Screen"
        }
    }

}


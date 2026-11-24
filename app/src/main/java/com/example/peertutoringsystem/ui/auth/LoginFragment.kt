package com.example.peertutoringsystem.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.util.SessionManager

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the real login layout
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // Find the views
        val etPhone = view.findViewById<EditText>(R.id.etLoginPhone)
        val etPassword = view.findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = view.findViewById<TextView>(R.id.tvGoToRegister)

        // Handle Login Button Click
        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Basic validation
            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter phone and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call ViewModel to check the database
            viewModel.loginUser(phone, password) { user ->
                requireActivity().runOnUiThread {
                    if (user != null) {
                        // SUCCESS: User exists in the database
                        // Save the session so they stay logged in
                        sessionManager.saveUser(user.userId, user.role)

                        Toast.makeText(requireContext(), "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()

                        // Navigate to Home
                        findNavController().navigate(R.id.action_login_to_home)
                    } else {
                        // FAILURE: Wrong phone or password
                        Toast.makeText(requireContext(), "Invalid phone number or password", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Handle "Go to Register" click
        tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }
}
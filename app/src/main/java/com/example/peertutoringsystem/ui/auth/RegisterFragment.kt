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
import androidx.navigation.fragment.navArgs
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.data.model.User
import com.example.peertutoringsystem.util.SessionManager

class RegisterFragment : Fragment() {

    // Initialize ViewModel and SessionManager
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    // Safe Args will automatically parse the "role" argument from the nav_graph
    private val args: RegisterFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the REAL layout, not the simple_list_item_1
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // 1. Find all views
        val etName = view.findViewById<EditText>(R.id.etName)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etLevel = view.findViewById<EditText>(R.id.etLevel)
        val etSubjects = view.findViewById<EditText>(R.id.etSubjects)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = view.findViewById<TextView>(R.id.tvGoToLogin)

        // 2. Check the role passed from RoleSelectFragment
        val userRole = args.role ?: "STUDENT" // Will be "STUDENT" or "TUTOR"

        // If the user is a Tutor, show the extra fields
        if (userRole == "TUTOR") {
            etLevel.visibility = View.VISIBLE
            etSubjects.visibility = View.VISIBLE
        } else {
            etLevel.visibility = View.GONE
            etSubjects.visibility = View.GONE
        }

        // 3. Handle the Register button click
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Basic validation (You can replace this with Member 3's Validator later)
            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the User object
            val newUser = User(
                name = name,
                phone = phone,
                password = password,
                role = userRole,
                level = if (userRole == "TUTOR") etLevel.text.toString().trim() else "",
                subjects = if (userRole == "TUTOR") etSubjects.text.toString().trim() else ""
            )

            // 4. Call the ViewModel to insert into Room Database
            viewModel.registerUser(newUser) { newId ->
                // This block runs when the database successfully saves the user
                requireActivity().runOnUiThread {
                    // Save the session so the user stays logged in
                    sessionManager.saveUser(newId.toInt(), userRole)

                    Toast.makeText(requireContext(), "Registration Successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to Home and clear the backstack (so they can't press back to Register)
                    findNavController().navigate(R.id.action_register_to_home)
                }
            }
        }

        // 5. Handle the "Already have an account? Login" text click
        tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }
}
package com.example.peertutoringsystem.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.ui.viewmodel.ProfileViewModel
import com.example.peertutoringsystem.util.SessionManager
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    private lateinit var tvRole: TextView
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLevel: EditText
    private lateinit var etSubjects: EditText
    private lateinit var layoutSubjectsContainer: LinearLayout
    private lateinit var tilName: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilLevel: TextInputLayout
    private lateinit var tilSubjects: TextInputLayout
    private lateinit var tvStatusMessage: TextView
    private lateinit var btnSaveProfile: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        tvRole = view.findViewById(R.id.tvRole)
        etName = view.findViewById(R.id.etName)
        etPhone = view.findViewById(R.id.etPhone)
        etLevel = view.findViewById(R.id.etLevel)
        etSubjects = view.findViewById(R.id.etSubjects)
        layoutSubjectsContainer = view.findViewById(R.id.layoutSubjectsContainer)
        tilName = view.findViewById(R.id.tilName)
        tilPhone = view.findViewById(R.id.tilPhone)
        tilLevel = view.findViewById(R.id.tilLevel)
        tilSubjects = view.findViewById(R.id.tilSubjects)
        tvStatusMessage = view.findViewById(R.id.tvStatusMessage)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)

        val loggedInUserId = sessionManager.getUserId()
        viewModel.loadUser(loggedInUserId)

        observeViewModel()

        btnSaveProfile.setOnClickListener {
            viewModel.saveProfile(
                name = etName.text.toString(),
                phone = etPhone.text.toString(),
                level = etLevel.text.toString(),
                subjects = etSubjects.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                tvRole.text = "Role: ${user.role}"
                etName.setText(user.name)
                etPhone.setText(user.phone)
                etLevel.setText(user.level)

                // Show/hide subjects field automatically based on role
                if (user.role == "TUTOR") {
                    layoutSubjectsContainer.visibility = View.VISIBLE
                    etSubjects.setText(user.subjects)
                } else {
                    layoutSubjectsContainer.visibility = View.GONE
                }
            }
        }

        viewModel.nameError.observe(viewLifecycleOwner) { error ->
            tilName.error = error
        }

        viewModel.phoneError.observe(viewLifecycleOwner) { error ->
            tilPhone.error = error
        }

        viewModel.levelError.observe(viewLifecycleOwner) { error ->
            tilLevel.error = error
        }

        viewModel.subjectsError.observe(viewLifecycleOwner) { error ->
            tilSubjects.error = error
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                tvStatusMessage.text = message
                tvStatusMessage.visibility = View.VISIBLE
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                tvStatusMessage.visibility = View.GONE
            }
        }

        viewModel.isSaving.observe(viewLifecycleOwner) { isSaving ->
            btnSaveProfile.isEnabled = !isSaving
        }
    }
}

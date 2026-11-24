package com.example.peertutoringsystem.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.peertutoringsystem.R

class RoleSelectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_role_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnStudent).setOnClickListener {
            // Safe Args uses the action ID from the XML
            val action = RoleSelectFragmentDirections.actionRoleSelectToRegister("STUDENT")
            findNavController().navigate(action)
        }

        view.findViewById<View>(R.id.btnTutor).setOnClickListener {
            val action = RoleSelectFragmentDirections.actionRoleSelectToRegister("TUTOR")
            findNavController().navigate(action)
        }
    }
}
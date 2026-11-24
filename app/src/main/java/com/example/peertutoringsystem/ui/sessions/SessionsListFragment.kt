package com.example.peertutoringsystem.ui.sessions

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.data.model.Session
import com.example.peertutoringsystem.ui.viewmodel.SessionViewModel
import java.util.Calendar

class SessionsListFragment : Fragment() {

    private val viewModel: SessionViewModel by viewModels()

    private lateinit var adapter: SessionAdapter
    private lateinit var tvEmpty: TextView

    // The list currently on screen (Upcoming or Past)
    private var currentSource: LiveData<List<Session>>? = null

    private val listObserver = Observer<List<Session>> { sessions ->
        adapter.submitList(sessions)
        tvEmpty.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sessions_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvEmpty = view.findViewById(R.id.tvEmpty)
        val rvSessions = view.findViewById<RecyclerView>(R.id.rvSessions)
        val rgTabs = view.findViewById<RadioGroup>(R.id.rgTabs)

        adapter = SessionAdapter(
            onCancel = { session -> confirmCancel(session) },
            onReschedule = { session -> startReschedule(session) },
            onComplete = { session -> viewModel.markCompleted(session) }
        )
        rvSessions.layoutManager = LinearLayoutManager(requireContext())
        rvSessions.adapter = adapter

        rgTabs.setOnCheckedChangeListener { _, checkedId ->
            showTab(upcoming = checkedId == R.id.rbUpcoming)
        }

        showTab(upcoming = true)
    }

    // Switch between the Upcoming and Past lists
    private fun showTab(upcoming: Boolean) {
        currentSource?.removeObserver(listObserver)
        val now = System.currentTimeMillis()
        val source = if (upcoming) viewModel.upcoming(now) else viewModel.past(now)
        currentSource = source
        source.observe(viewLifecycleOwner, listObserver)
    }

    private fun confirmCancel(session: Session) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel session?")
            .setMessage("This session will be marked as cancelled.")
            .setPositiveButton("Yes") { _, _ -> viewModel.cancelSession(session.sessionId) }
            .setNegativeButton("No", null)
            .show()
    }

    // Reschedule: pick a new date, then a new time, then confirm the link or location
    private fun startReschedule(session: Session) {
        val cal = Calendar.getInstance().apply { timeInMillis = session.dateTime }

        DatePickerDialog(requireContext(), { _, year, month, day ->
            cal.set(year, month, day)

            TimePickerDialog(requireContext(), { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                askLocation(session, cal.timeInMillis)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    private fun askLocation(session: Session, newDateTime: Long) {
        val input = EditText(requireContext())
        input.setText(session.locationOrLink)

        AlertDialog.Builder(requireContext())
            .setTitle("Link or location")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val error = viewModel.rescheduleSession(
                    session.sessionId, newDateTime, input.text.toString()
                )
                if (error != null) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Back", null)
            .show()
    }
}
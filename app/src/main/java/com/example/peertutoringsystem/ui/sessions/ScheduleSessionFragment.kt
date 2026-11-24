package com.example.peertutoringsystem.ui.sessions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.ui.viewmodel.SessionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleSessionFragment : Fragment() {

    private val viewModel: SessionViewModel by viewModels()

    // Passed in by whoever opens this screen (Accept button). 0 = not passed (testing)
    private val requestId: Int get() = arguments?.getInt("requestId", 0) ?: 0
    private val offerId: Int get() = arguments?.getInt("offerId", 0) ?: 0

    private val chosen: Calendar = Calendar.getInstance()
    private var dateChosen = false
    private var timeChosen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_schedule_session, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDate = view.findViewById<Button>(R.id.btnPickDate)
        val btnTime = view.findViewById<Button>(R.id.btnPickTime)
        val rgMode = view.findViewById<RadioGroup>(R.id.rgMode)
        val etLocation = view.findViewById<EditText>(R.id.etLocationOrLink)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val btnSchedule = view.findViewById<Button>(R.id.btnSchedule)

        btnDate.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                chosen.set(Calendar.YEAR, year)
                chosen.set(Calendar.MONTH, month)
                chosen.set(Calendar.DAY_OF_MONTH, day)
                dateChosen = true
                btnDate.text = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                    .format(chosen.time)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                .show()
        }

        btnTime.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hour, minute ->
                chosen.set(Calendar.HOUR_OF_DAY, hour)
                chosen.set(Calendar.MINUTE, minute)
                timeChosen = true
                btnTime.text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }

        // Change the hint to match the chosen mode
        rgMode.setOnCheckedChangeListener { _, checkedId ->
            etLocation.hint =
                if (checkedId == R.id.rbOnline) "Meeting link" else "Meeting location"
        }

        btnSchedule.setOnClickListener {
            tvError.visibility = View.GONE

            if (!dateChosen || !timeChosen) {
                showError(tvError, "Select a date and a time")
                return@setOnClickListener
            }

            chosen.set(Calendar.SECOND, 0)
            chosen.set(Calendar.MILLISECOND, 0)

            val mode =
                if (rgMode.checkedRadioButtonId == R.id.rbOnline) "ONLINE" else "IN_PERSON"

            val error = viewModel.scheduleSession(
                offerId = offerId,
                requestId = requestId,
                dateTime = chosen.timeInMillis,
                mode = mode,
                locationOrLink = etLocation.text.toString()
            )

            if (error != null) {
                showError(tvError, error)
            } else {
                Toast.makeText(requireContext(), "Session scheduled", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.sessionsListFragment)
            }
        }
    }

    private fun showError(tv: TextView, message: String) {
        tv.text = message
        tv.visibility = View.VISIBLE
    }
}
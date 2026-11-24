package com.example.peertutoringsystem.ui.sessions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.data.model.Session
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Shows one card per session. The three buttons only appear on SCHEDULED sessions.
class SessionAdapter(
    private val onCancel: (Session) -> Unit,
    private val onReschedule: (Session) -> Unit,
    private val onComplete: (Session) -> Unit
) : ListAdapter<Session, SessionAdapter.SessionHolder>(DIFF) {

    class SessionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvModeLocation: TextView = view.findViewById(R.id.tvModeLocation)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val layoutButtons: View = view.findViewById(R.id.layoutButtons)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
        val btnReschedule: Button = view.findViewById(R.id.btnReschedule)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session, parent, false)
        return SessionHolder(view)
    }

    override fun onBindViewHolder(holder: SessionHolder, position: Int) {
        val session = getItem(position)

        holder.tvDateTime.text = SimpleDateFormat("EEE, dd MMM yyyy  HH:mm", Locale.getDefault())
            .format(Date(session.dateTime))

        val modeText = if (session.mode == "ONLINE") "Online" else "In person"
        holder.tvModeLocation.text = "$modeText: ${session.locationOrLink}"

        holder.tvStatus.text = session.status

        // Only scheduled sessions can be cancelled, rescheduled or completed
        holder.layoutButtons.visibility =
            if (session.status == "SCHEDULED") View.VISIBLE else View.GONE

        holder.btnCancel.setOnClickListener { onCancel(session) }
        holder.btnReschedule.setOnClickListener { onReschedule(session) }
        holder.btnComplete.setOnClickListener { onComplete(session) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Session>() {
            override fun areItemsTheSame(old: Session, new: Session) =
                old.sessionId == new.sessionId

            override fun areContentsTheSame(old: Session, new: Session) = old == new
        }
    }
}
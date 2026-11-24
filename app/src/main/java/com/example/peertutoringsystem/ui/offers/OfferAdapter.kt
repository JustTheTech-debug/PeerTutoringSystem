package com.example.peertutoringsystem.ui.offers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.data.model.TutorOffer

class OfferAdapter(private val onAccept: (TutorOffer) -> Unit) :
    ListAdapter<TutorOffer, OfferAdapter.Holder>(Diff) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val fee: TextView = view.findViewById(R.id.feeText)
        val message: TextView = view.findViewById(R.id.messageText)
        val status: TextView = view.findViewById(R.id.statusText)
        val accept: Button = view.findViewById(R.id.acceptButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_offer, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val offer = getItem(position)
        holder.fee.text = "KSh ${offer.fee}"
        holder.message.text = offer.message
        holder.status.text = "Status: ${offer.status}"
        // Only pending offers can be accepted
        holder.accept.visibility = if (offer.status == "PENDING") View.VISIBLE else View.GONE
        holder.accept.setOnClickListener { onAccept(offer) }
    }

    object Diff : DiffUtil.ItemCallback<TutorOffer>() {
        override fun areItemsTheSame(a: TutorOffer, b: TutorOffer) = a.offerId == b.offerId
        override fun areContentsTheSame(a: TutorOffer, b: TutorOffer) = a == b
    }
}
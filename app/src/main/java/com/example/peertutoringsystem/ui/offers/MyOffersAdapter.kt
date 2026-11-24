package com.example.peertutoringsystem.ui.offers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.peertutoringsystem.R
import com.example.peertutoringsystem.data.model.TutorOffer

class MyOffersAdapter(
    private val onEditClick: (TutorOffer) -> Unit,
    private val onWithdrawClick: (TutorOffer) -> Unit
) : RecyclerView.Adapter<MyOffersAdapter.OfferViewHolder>() {

    private val offers = mutableListOf<TutorOffer>()

    fun submitList(newList: List<TutorOffer>) {
        offers.clear()
        offers.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_offer, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(offers[position])
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRequestId: TextView = itemView.findViewById(R.id.tvRequestId)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvFee: TextView = itemView.findViewById(R.id.tvFee)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEditOffer)
        private val btnWithdraw: Button = itemView.findViewById(R.id.btnWithdrawOffer)

        fun bind(offer: TutorOffer) {
            tvRequestId.text = "Request #${offer.requestId}"
            tvStatus.text = offer.status
            tvFee.text = "Fee: $${offer.fee}"
            tvMessage.text = offer.message

            // Disable / hide edit and withdraw if accepted or withdrawn
            if (offer.status == "ACCEPTED" || offer.status == "WITHDRAWN") {
                btnEdit.visibility = View.GONE
                btnWithdraw.visibility = View.GONE
            } else {
                btnEdit.visibility = View.VISIBLE
                btnWithdraw.visibility = View.VISIBLE

                btnEdit.setOnClickListener { onEditClick(offer) }
                btnWithdraw.setOnClickListener { onWithdrawClick(offer) }
            }
        }
    }
}

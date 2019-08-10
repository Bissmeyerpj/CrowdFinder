package com.example.crowdfinder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_request.view.*

class RequestViewHolder(itemView: View, val adapter: RequestListAdapter) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.request_name_text_view as TextView
    private val acceptButton = itemView.request_accept_button as Button
    private val declineButton = itemView.request_decline_button as Button

    fun bind(friend: Friend) {
        val requestString = String.format("Request From: %s", friend.name)
        titleTextView.text = requestString
        acceptButton.setOnClickListener {
            Log.d(Constants.TAG, "request accepted")
            adapter.selectRequestAt(adapterPosition, true)
        }
        declineButton.setOnClickListener {
            Log.d(Constants.TAG, "request denied")
            adapter.selectRequestAt(adapterPosition, false)
        }
    }
}
package com.example.crowdfinder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_friend.view.*
import kotlinx.android.synthetic.main.row_view_request.view.*

class RequestViewHolder(itemView: View, adapter: RequestListAdapter) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.request_name_text_view as TextView

    init {
        itemView.setOnClickListener {
            adapter.selectRequestAt(adapterPosition)
        }
    }

    fun bind(request: Request) {
        val requestString = String.format("Request From: %s", request.friend.name)
        titleTextView.text = requestString
    }
}
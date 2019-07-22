package com.example.crowdfinder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_friend.view.*

class FriendViewHolder(itemView: View, adapter: FriendListAdapter) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.friend_name_text_view as TextView

    init {
        itemView.setOnClickListener {
            adapter.selectDocAt(adapterPosition)
        }
    }

    fun bind(friend: Friend) {
        titleTextView.text = friend.name
    }
}
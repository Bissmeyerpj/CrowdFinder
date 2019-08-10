package com.example.crowdfinder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.row_view_friend.view.*

class FriendViewHolder(itemView: View, adapter: FriendListAdapter) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.friend_name_text_view as TextView
    private val emailTextView = itemView.friend_email_text_view as TextView

    init {
        itemView.setOnClickListener {
            adapter.selectFriendAt(adapterPosition)
        }
        itemView.setOnLongClickListener {
            adapter.showDeleteDialog(adapterPosition)
            true
        }
    }

    fun bind(friend: Friend) {
        titleTextView.text = friend.name
        emailTextView.text = friend.email
    }
}
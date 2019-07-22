package com.example.crowdfinder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class FriendListAdapter(var context: Context?, var listener: FriendListFragment.OnFriendSelectedListener?) : RecyclerView.Adapter<FriendViewHolder>() {

    var friends = ArrayList<Friend>()

    init {
        friends.add(Friend("TEST FRIEND 1"))
        friends.add(Friend("TEST FRIEND 2"))
        friends.add(Friend("TEST FRIEND 3"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_friend, parent, false)
        return FriendViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friends[position])
    }
    override fun getItemCount() = friends.size

    fun selectDocAt(adapterPosition: Int) {
        val doc = friends[adapterPosition]
        listener?.onFriendSelected(doc)
    }
}
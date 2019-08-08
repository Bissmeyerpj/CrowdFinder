package com.example.crowdfinder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FriendListAdapter(var context: Context?, var listener: FriendListFragment.OnFriendSelectedListener?, var email: String) : RecyclerView.Adapter<FriendViewHolder>() {

    var friends = ArrayList<Friend>()
    var friendRef = FirebaseFirestore.getInstance().collection(Constants.USERS)
        .document(email).collection(Constants.FRIENDS)
    val usersRef = FirebaseFirestore.getInstance().collection(Constants.USERS)

    init {
        friendRef.get().addOnSuccessListener { result ->
            for(doc in result) {
                usersRef.document(doc.id).get().addOnSuccessListener {
                    friends.add(Friend(it.getString(Constants.NICKNAME)?:"", doc.id))
                    notifyDataSetChanged()
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_friend, parent, false)
        return FriendViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friends[position])
    }
    override fun getItemCount() = friends.size

    fun selectFriendAt(adapterPosition: Int) {
        val friend = friends[adapterPosition]
        listener?.onFriendSelected(friend)
    }
}
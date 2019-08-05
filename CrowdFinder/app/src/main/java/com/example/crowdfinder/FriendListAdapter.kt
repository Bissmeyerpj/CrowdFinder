package com.example.crowdfinder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FriendListAdapter(var context: Context?, var listener: FriendListFragment.OnFriendSelectedListener?) : RecyclerView.Adapter<FriendViewHolder>() {

    var friends = ArrayList<Friend>()
    var friendRef = FirebaseFirestore.getInstance().collection(Constants.USERS)
        .document(listener!!.getEmail()).collection(Constants.FRIENDS)

    init {
        friendRef.get().addOnSuccessListener { result ->
            for(doc in result) {
                val text = String.format("email: %s, nickname: %s", doc.id, doc.get("nickname"))
                Log.d(Constants.TAG, text)
                friends.add(Friend(doc.get("nickname").toString(), doc.id))
            }
            notifyDataSetChanged()
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
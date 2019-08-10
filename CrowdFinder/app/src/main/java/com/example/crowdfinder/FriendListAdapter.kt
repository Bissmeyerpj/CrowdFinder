package com.example.crowdfinder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog_add_friend.view.*

class FriendListAdapter(
    var context: Context?,
    var listener: FriendListFragment.OnFriendSelectedListener?,
    var email: String
) : RecyclerView.Adapter<FriendViewHolder>() {

    var friends = ArrayList<Friend>()
    var friendRef = FirebaseFirestore.getInstance().collection(Constants.USERS)
        .document(email).collection(Constants.FRIENDS)
    val userRef = FirebaseFirestore.getInstance().collection(Constants.USERS)

    private var listenerRegistration: ListenerRegistration

    init {
        listenerRegistration = friendRef
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        for (documentChange in querySnapshot.documentChanges) {
            val friend =
                Friend(documentChange.document.getString(Constants.NICKNAME) ?: "failure", documentChange.document.id)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    friends.add(0, friend)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    val index = friends.indexOfFirst { it.email == friend.email }
                    friends.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    val index = friends.indexOfFirst { it.email == friend.email }
                    friends[index] = friend
                    notifyItemChanged(index)
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

    fun showDeleteDialog(pos: Int) {
        val builder = android.support.v7.app.AlertDialog.Builder(context!!)
        builder.setTitle(R.string.remove_friend_title)

        builder.setMessage(String.format("Would you like to remove %s from your friend list?", friends[pos].email))

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            friendRef.document(friends[pos].email).delete()
            userRef.document(friends[pos].email).collection(Constants.FRIENDS).document(email).delete()
            Toast.makeText(
                context,
                "Friend Deleted",
                Toast.LENGTH_LONG
            ).show()

        }
        builder.create().show()
    }
}
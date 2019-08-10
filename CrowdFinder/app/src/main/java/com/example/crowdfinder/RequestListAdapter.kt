package com.example.crowdfinder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class RequestListAdapter(var context: Context?, var listener: RequestListFragment.OnRequestSelectedListener?, var email: String) : RecyclerView.Adapter<RequestViewHolder>() {

    val requests = ArrayList<Friend>()
    val requestRef = FirebaseFirestore.getInstance().collection(Constants.USERS)
        .document(email).collection(Constants.REQUESTS)
    private var listenerRegistration: ListenerRegistration

    init {
        listenerRegistration = requestRef
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val friend = Friend(documentChange.document.id, documentChange.document.id)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    requests.add(0, friend)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    val index = requests.indexOfFirst { it.email == friend.email }
                    requests.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    val index = requests.indexOfFirst { it.email == friend.email }
                    requests[index] = friend
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_request, parent, false)
        return RequestViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }
    override fun getItemCount() = requests.size

    fun selectRequestAt(adapterPosition: Int, accepted: Boolean) {
        val friend = requests[adapterPosition]
        listener?.onRequestSelected(friend, accepted)
    }
}
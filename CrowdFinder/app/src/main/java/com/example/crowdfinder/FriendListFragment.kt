package com.example.crowdfinder


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class FriendListFragment : Fragment() {

    private var listener: OnFriendSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_friend_list, container, false) as RecyclerView
        val adapter = FriendListAdapter(context, listener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        return recyclerView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFriendSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFriendSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFriendSelectedListener {
        fun onFriendSelected(friend: Friend)
        fun getEmail() : String
    }

}
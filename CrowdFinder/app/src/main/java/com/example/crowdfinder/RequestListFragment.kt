package com.example.crowdfinder


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class RequestListFragment : Fragment() {
    private var listener: OnRequestSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_request_list, container, false) as RecyclerView
        val email = listener?.getEmail() ?: ""
        val adapter = RequestListAdapter(context, listener, email)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        return recyclerView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRequestSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnRequestSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnRequestSelectedListener {
        fun onRequestSelected(friend: Friend, accepted: Boolean)
        fun getEmail(): String
    }

}
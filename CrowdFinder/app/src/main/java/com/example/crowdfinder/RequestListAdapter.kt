package com.example.crowdfinder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class RequestListAdapter(var context: Context?, var listener: RequestListFragment.OnRequestSelectedListener?) : RecyclerView.Adapter<RequestViewHolder>() {

    var requests = ArrayList<Request>()

    init {
        requests.add(Request(Friend("Test1")))
        requests.add(Request(Friend("Test2")))
        requests.add(Request(Friend("Test3")))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_request, parent, false)
        return RequestViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }
    override fun getItemCount() = requests.size

    fun selectRequestAt(adapterPosition: Int) {
        val request = requests[adapterPosition]
        listener?.onRequestSelected(request)
    }
}
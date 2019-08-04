package com.example.crowdfinder


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_compass.view.*
import kotlinx.android.synthetic.main.row_view_friend.view.*


class CompassFragment : Fragment() {

    val friend = Friend("MyFriend")
    lateinit var text: String

    private var listener: LocationStringListener? = null
    interface LocationStringListener {
        fun getLocation(): String
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_compass, container, false)
        view.currently_tracked_friend_textview.text = friend.name
        view.current_distance_textview.text = listener?.getLocation()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LocationStringListener) {
            listener = context
            Log.d(Constants.TAG, "context is listener")
        } else {
            throw RuntimeException(context.toString() + " must implement LocationStringListener")
        }
    }


}

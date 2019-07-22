package com.example.crowdfinder


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_compass.view.*
import kotlinx.android.synthetic.main.row_view_friend.view.*


class CompassFragment : Fragment() {

    val friend = Friend("MyFriend")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_compass, container, false)
        view.currently_tracked_friend_textview.text = friend.name
        return view
    }


}

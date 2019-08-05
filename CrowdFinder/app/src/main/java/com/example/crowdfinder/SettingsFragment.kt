package com.example.crowdfinder


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    var listener: SettingsListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        view.logout_button.setOnClickListener {
            val loginIntent = Intent(context, MainActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(loginIntent)
        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is SettingsListener) {
            listener = context
        }
    }

    interface SettingsListener {
        fun getEmail() : String
    }

}

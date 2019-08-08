package com.example.crowdfinder


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_settings.view.*
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import com.google.common.reflect.Reflection.getPackageName


class SettingsFragment : Fragment() {

    var listener: SettingsListener? = null
    lateinit var nicknameRef: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val email = listener!!.getEmail()
        view.email_text_view.setText(email)
        nicknameRef = FirebaseFirestore.getInstance().collection(Constants.USERS).document(email)
        nicknameRef.get().addOnSuccessListener {
            view.nickname_edit_text.setText(it.getString(Constants.NICKNAME))
        }
        view.change_nickname_button.setOnClickListener {
            changeNicknameHandler()
        }
//        view.location_button.setOnClickListener {
//            val intent = Intent()
//            intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
//            val uri = Uri.fromParts("package", getPackageName(), null)
//            intent.data = uri
//            startActivity(intent)
//        }
        view.logout_button.setOnClickListener {
            listener?.logout()
        }
        return view
    }

    private fun changeNicknameHandler() {
        val map = mapOf<String, Any>(Constants.NICKNAME to view?.nickname_edit_text?.text.toString())
        nicknameRef.set(map)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is SettingsListener) {
            listener = context
        }
    }

    interface SettingsListener {
        fun getEmail() : String
        fun logout()
    }

}

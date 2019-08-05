package com.example.crowdfinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val loginRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.LOGINS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_button.setOnClickListener {
            checkLoginInfo()
        }

        sign_up_button.setOnClickListener {
            val createAccountIntent = Intent(this, CreateAccountActivity::class.java)
            startActivity(createAccountIntent)
        }

    }

    private fun checkLoginInfo() {
        loginRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val doc = document.toObject(Login::class.java)
                    if(doc.email == email_edit_text.text.toString()
                        && doc.password == password_edit_text.text.toString()) {
                        login(doc.email)
                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.d(Constants.TAG, "Error getting documents: ", exception)
            }

    }

    private fun login(email: String) {
        val bottomNavIntent = Intent(this, BottomNavActivity::class.java)
        val bundle = Bundle()
        bundle.putString("email", email)
        bottomNavIntent.putExtras(bundle)
        startActivity(bottomNavIntent)
    }

}

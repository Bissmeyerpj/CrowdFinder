package com.example.crowdfinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    val loginRef = FirebaseFirestore.getInstance().collection(Constants.LOGINS)
    val usersRef = FirebaseFirestore.getInstance().collection(Constants.USERS)
    val locationRef = FirebaseFirestore.getInstance().collection(Constants.LOCATIONS)
    lateinit var nicknameET: EditText
    lateinit var emailET: EditText
    lateinit var passwordET: EditText
    lateinit var passwordRET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        nicknameET = nickname_edit_text
        emailET = email_edit_text
        passwordET = password_edit_text
        passwordRET = password_repeat_edit_text

        create_account_button.setOnClickListener {
            usersRef.get().addOnSuccessListener { result ->
                for (doc in result) {
                    if (doc.id == emailET.text.toString()) {
                        Toast.makeText(
                            this,
                            "This email is already in use",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnSuccessListener
                    }
                }
                if (passwordET.text.toString() != passwordRET.text.toString()) {
                    Toast.makeText(
                        this,
                        "Passwords do not match",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }
                handleAccountCreate()
            }
        }
    }

    private fun handleAccountCreate() {
        loginRef.add(Login(emailET.text.toString(), passwordET.text.toString()))
        val map1 = mapOf<String, Any>(Constants.LATLONG to "0 : 0")
        locationRef.document(emailET.text.toString()).set(map1)
        val map2 = mapOf<String, Any>("nickname" to nicknameET.text.toString())
        usersRef.document(emailET.text.toString()).set(map2)
        val bottomNavIntent = Intent(this, BottomNavActivity::class.java)
        val bundle = Bundle()
        bundle.putString("email", emailET.text.toString())
        bottomNavIntent.putExtras(bundle)
        startActivity(bottomNavIntent)
    }

}

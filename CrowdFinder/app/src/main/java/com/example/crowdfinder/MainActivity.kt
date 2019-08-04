package com.example.crowdfinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_button.setOnClickListener {
            val bottomNavIntent = Intent(this, BottomNavActivity::class.java)
            startActivity(bottomNavIntent)
        }

        sign_up_button.setOnClickListener {
            val createAccountIntent = Intent(this, CreateAccountActivity::class.java)
            startActivity(createAccountIntent)
        }
    }
}

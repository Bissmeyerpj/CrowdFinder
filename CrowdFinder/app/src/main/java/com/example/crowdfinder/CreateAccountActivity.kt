package com.example.crowdfinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        create_account_button.setOnClickListener {
            val bottomNavIntent = Intent(this, BottomNavActivity::class.java)
            startActivity(bottomNavIntent)
        }
    }
}

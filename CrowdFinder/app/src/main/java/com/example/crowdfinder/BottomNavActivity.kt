package com.example.crowdfinder

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_bottom_nav.*

class BottomNavActivity : AppCompatActivity(),
        FriendListFragment.OnFriendSelectedListener,
        RequestListFragment.OnRequestSelectedListener
{
    override fun onRequestSelected(request: Request) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var currentFriend = Friend("Dummy Name")

    override fun onFriendSelected(friend: Friend) {
        TODO("not implemented")
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var switchTo: Fragment? = null
        when (item.itemId) {
            R.id.settings -> {
                switchTo = SettingsFragment()
            }
            R.id.compass -> {
                switchTo = CompassFragment()
            }
            R.id.friends -> {
                switchTo = FriendListFragment()
            }
            R.id.requests -> {
                switchTo = RequestListFragment()
            }
        }
        switchTo?.let {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo, "MY_FRAGMENT")
            while(supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val frag = CompassFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, frag, "MY_FRAGMENT")
        ft.commit()
    }

}

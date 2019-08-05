package com.example.crowdfinder

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_bottom_nav.*

class BottomNavActivity : AppCompatActivity(),
        FriendListFragment.OnFriendSelectedListener,
        RequestListFragment.OnRequestSelectedListener,
        CompassFragment.LocationStringListener
{

    private lateinit var email: String
    private var locationString = ""
    private var currentFriend = Friend("Dummy Name")
    private lateinit var locationRef: DocumentReference

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var switchTo: Fragment? = null
        when (item.itemId) {
            R.id.settings -> {
                switchTo = SettingsFragment()
            }
            R.id.compass -> {
                switchTo = CompassFragment()
                lastLocation()
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.selectedItemId = R.id.friends

        val frag = FriendListFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, frag, "MY_FRAGMENT")
        ft.commit()

        email = intent.extras.getString("email")
        locationRef = FirebaseFirestore.getInstance().collection(Constants.LOCATIONS).document(email)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private fun lastLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(Constants.TAG, "Successful location")
                val thingy = location?.latitude.toString() + " : " + location?.longitude.toString()
                val map = mapOf<String, Any>(Constants.LATLONG to thingy)
                locationRef.set(map)
            }
            .addOnFailureListener {
                Log.d(Constants.TAG, "Failed location")
            }
    }

    override fun getLocation(): String = locationString

    override fun onRequestSelected(request: Request) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFriendSelected(friend: Friend) {
        currentFriend = friend
        val friendLocationRef = FirebaseFirestore.
            getInstance().
            collection(Constants.LOCATIONS).
            document(currentFriend.email)

        friendLocationRef.get().addOnSuccessListener {
            locationString = it.get(Constants.LATLONG).toString()
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, CompassFragment(), "MY_FRAGMENT")
        ft.commit()
    }

    override fun getEmail(): String {
        return email
    }

    override fun getFriend(): Friend {
        return currentFriend
    }

}

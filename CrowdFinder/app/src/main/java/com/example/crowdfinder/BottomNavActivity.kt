package com.example.crowdfinder

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_bottom_nav.*
import kotlinx.android.synthetic.main.content_bottom_nav.*
import kotlinx.android.synthetic.main.dialog_add_friend.view.*

class BottomNavActivity : AppCompatActivity(),
        FriendListFragment.OnFriendSelectedListener,
        RequestListFragment.OnRequestSelectedListener,
        CompassFragment.LocationStringListener,
        SettingsFragment.SettingsListener,
        SplashFragment.OnLoginButtonPressedListener,
        SensorEventListener
{
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not used
    }

    private lateinit var mSensorManager : SensorManager

    override fun onResume() {
        super.onResume()

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(
            this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // get the angle around the z-axis rotated
        val degree = Math.round(event!!.values[0])
        heading = degree.toFloat()
    }

    private var email: String = ""
    private var locationString = "N/A"
    private var heading = 0.0f
    private var currentFriend: Friend? = null
    private var locationRef = FirebaseFirestore.getInstance().collection(Constants.LOCATIONS)
    private val usersRef = FirebaseFirestore.getInstance().collection(Constants.USERS)

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
        if (auth.currentUser != null) {
            switchTo?.let {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, switchTo, "MY_FRAGMENT")
                while (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStackImmediate()
                }
                ft.commit()
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)
        initializeListeners()
        fab.setOnClickListener { view ->
            handleFabClick()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.selectedItemId = R.id.friends

        if(auth.currentUser!=null) {
            email = auth.currentUser?.email.toString()
            Log.d(Constants.TAG, auth.currentUser?.displayName.toString())
        }

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private fun lastLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(Constants.TAG, "Successful location")
                val map = mapOf<String, Any>(Constants.LAT to location!!.latitude, Constants.LONG to location!!.longitude)
                locationRef.document(email).set(map)
            }
            .addOnFailureListener {
                Log.d(Constants.TAG, String.format("Failed location: %s", it.toString()))
            }
    }

    override fun getLocation(): String {
        lastLocation()
        return locationString
    }

    override fun getHeading(): Float {
        return heading
    }

    override fun onRequestSelected(friend: Friend, accepted: Boolean) {
        val state = if(accepted) "accepted" else "denied"
        Log.d(Constants.TAG, String.format("%s request from %s", state, friend.name))
        if (accepted) addFriend(friend.email)
        usersRef.document(email).collection(Constants.REQUESTS).document(friend.email).delete()
    }

    override fun onFriendSelected(friend: Friend) {
        currentFriend = friend
        val ft = supportFragmentManager.beginTransaction()
        val cf = CompassFragment()
        ft.replace(R.id.fragment_container, cf, "MY_FRAGMENT")
        ft.commit()
        nav_view.selectedItemId = R.id.compass
        Toast.makeText(
            this,
            String.format("Tracking %s, Press REFRESH", friend.name),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getEmail(): String {
        return email
    }

    override fun getFriend(): Friend? {
        return currentFriend
    }

    private fun handleFabClick() {
        val builder = android.support.v7.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.add_friend_title)

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend, null, false)
        builder.setView(view)

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val friendEmail = view.add_friend_email.text.toString()
            usersRef.get().addOnSuccessListener { result ->
                for (doc in result) {
                    if (doc.id == friendEmail) {
                        sendRequest(friendEmail)
                        return@addOnSuccessListener
                    }
                }
                Toast.makeText(
                    this,
                    "No account with that email",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        builder.create().show()
    }

    private fun addFriend(theirEmail: String) {
        usersRef.document(theirEmail).get().addOnSuccessListener {
            val map1 = mapOf(Constants.STATE to true, Constants.NICKNAME to (it.getString(Constants.NICKNAME)?:"ERROR"))
            usersRef.document(email).collection(Constants.FRIENDS).document(theirEmail).set(map1)
        }
        usersRef.document(email).get().addOnSuccessListener {
            val map2 = mapOf(Constants.STATE to true, Constants.NICKNAME to (it.getString(Constants.NICKNAME)?:"ERROR"))
            usersRef.document(theirEmail).collection(Constants.FRIENDS).document(email).set(map2)
        }
    }

    private fun sendRequest(theirEmail: String) {
        val requestRef = FirebaseFirestore.getInstance().collection(Constants.USERS).document(theirEmail).collection(Constants.REQUESTS)
            .document(email)
        val map = mapOf<String, Any>(Constants.STATE to true)
        requestRef.set(map)
    }

    //Authorization through google auth

    val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener
    private val RC_SIGN_IN = 1

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }

    private fun launchLoginUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent

        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.logo)
            .build()

        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    private fun initializeListeners() {
        authListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if(user!=null) {
                setupUser(user)
            } else {
                switchToSplashFragment()
            }
        }

    }

    private fun setupUser(user: FirebaseUser) {
        email = user.email.toString()
        usersRef.document(email).get().addOnSuccessListener { result ->
            if (result.getString("nickname") == null) {
                val map = mapOf<String, Any>("nickname" to email)
                usersRef.document(email).set(map)
            }
        }
        switchToFriendsListFragment()
    }

    override fun logout() {
        auth.signOut()
    }

    private fun switchToSplashFragment() {
        nav_view.selectedItemId = R.id.friends
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    private fun switchToFriendsListFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, FriendListFragment())
        ft.commit()
    }

}

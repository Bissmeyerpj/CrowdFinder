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
import android.view.LayoutInflater
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_bottom_nav.*
import kotlinx.android.synthetic.main.content_bottom_nav.*
import kotlinx.android.synthetic.main.dialog_add_friend.view.*

class BottomNavActivity : AppCompatActivity(),
        FriendListFragment.OnFriendSelectedListener,
        RequestListFragment.OnRequestSelectedListener,
        CompassFragment.LocationStringListener,
        SettingsFragment.SettingsListener,
        SplashFragment.OnLoginButtonPressedListener
{

    private lateinit var email: String
    private var locationString = ""
    private var currentFriend = Friend("Dummy Name")
    private var locationRef = FirebaseFirestore.getInstance().collection(Constants.LOCATIONS)
    val usersRef = FirebaseFirestore.getInstance().collection(Constants.USERS)

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
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.selectedItemId = R.id.friends

        if(auth.currentUser!=null) {
            email = auth.currentUser?.email.toString()
            Log.d(Constants.TAG, auth.currentUser?.displayName.toString())
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private fun lastLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(Constants.TAG, "Successful location")
                val thingy = location?.latitude.toString() + " : " + location?.longitude.toString()
                val map = mapOf<String, Any>(Constants.LATLONG to thingy)
                locationRef.document(email).set(map)
            }
            .addOnFailureListener {
                Log.d(Constants.TAG, String.format("Failed location: %s", it.toString()))
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
                        addFriend(friendEmail)
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
        val map1 = mapOf<String, Any>(Constants.STATE to true)
        usersRef.document(email).collection(Constants.FRIENDS).document(theirEmail).set(map1)
        val map2 = mapOf<String, Any>(Constants.STATE to true)
        usersRef.document(theirEmail).collection(Constants.FRIENDS).document(email).set(map2)
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
        if (::authListener.isInitialized) auth.addAuthStateListener(authListener)
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

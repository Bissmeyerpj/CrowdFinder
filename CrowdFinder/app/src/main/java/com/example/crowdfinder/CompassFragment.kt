package com.example.crowdfinder


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_compass.*
import kotlinx.android.synthetic.main.fragment_compass.view.*
import kotlinx.android.synthetic.main.row_view_friend.view.*
import kotlin.math.roundToInt
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import kotlin.math.pow
import kotlin.math.sqrt


class CompassFragment : Fragment() {

    private var friend: Friend? = null
    var location: String = "Lat : Long"
    var heading = 0.0f
    private var theirLat: Double = 10.0
    private var theirLong: Double = 10.0
    private var myLat: Double = 10.0
    private var myLong: Double = 10.0
    private var currentDegree = 0.0

    private var listener: LocationStringListener? = null
    interface LocationStringListener {
        fun getLocation(): String
        fun getHeading(): Float
        fun getFriend(): Friend?
        fun getEmail(): String
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_compass, container, false)

        view.refresh_button.setOnClickListener {
            refresh()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        view?.currently_tracked_friend_textview?.text = friend?.name
        view?.current_distance_textview?.text = location
        if (context is LocationStringListener) {
            listener = context
            Log.d(Constants.TAG, "context is listener")
        } else {
            throw RuntimeException(context.toString() + " must implement LocationStringListener")
        }
    }

    fun refresh() {
        friend = listener?.getFriend()
        view?.currently_tracked_friend_textview?.text = friend?.name ?: "No One!"

        getLocations()
    }

    private fun getLocations() {
        friend?.let { result ->
            val friendLocationRef =
                FirebaseFirestore.getInstance().collection(Constants.LOCATIONS).document(result.email)
            val myLocationRef =
                FirebaseFirestore.getInstance().collection(Constants.LOCATIONS).document(listener!!.getEmail())
            friendLocationRef.get().addOnSuccessListener {them ->
                theirLat = them.getDouble(Constants.LAT)!!
                theirLong = them.getDouble(Constants.LONG)!!
//                location = String.format("%s : %s", theirLat.toString(), theirLong.toString())
//                view?.current_distance_textview?.text = location
                myLocationRef.get().addOnSuccessListener {
                    myLat = it.getDouble(Constants.LAT)!!
                    myLong = it.getDouble(Constants.LONG)!!
                    calculateAndPost()
                }
            }
        }
    }

    private fun calculateAndPost() {

        heading = listener?.getHeading() ?: heading

        val deltaLat = theirLat - myLat
        val deltaLong = theirLong - myLong

        var angle = Math.atan(deltaLat/deltaLong)
        angle *= (180/3.14159)
        if (deltaLong > 0) {
            angle = 90 - angle
        } else {
            angle = 270 - angle
        }

        val rotationHeading = angle - heading

        val ra = RotateAnimation(
            currentDegree.toFloat(),
            rotationHeading.toFloat(),
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        val dist = sqrt(deltaLat.pow(2.0) + deltaLong.pow(2.0)) * 364320
        val distInt = dist.roundToInt()

        ra.duration = 210
        ra.fillAfter = true
        view?.compass_display?.startAnimation(ra)

        currentDegree = rotationHeading

//        view?.current_distance_textview?.text = String.format("needed heading: %s", angle.toString())

        view?.current_distance_textview?.text = String.format("%s feet", distInt.toString())

//        view?.current_heading_display?.text = String.format("current heading: %s", heading.toString())
    }


}

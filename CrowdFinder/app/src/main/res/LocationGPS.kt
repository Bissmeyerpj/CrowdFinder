import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationGPS {


    var latitude: Any? = null
    var longitude: Any? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun onCreate(savedInstanceState: Bundle?) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(thsis)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        fusedLocationClient.lastLocation.
            addOnSuccessListener { location: Location? ->
                latitude =  location?.latitude
                longitude = location?.longitude
            }
    }


}

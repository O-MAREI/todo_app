import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.example.geolocation.viewmodels.LocationViewModel

/**
 * A listener to constantly check for location updates. Has access to the viewModel
 */
object GeoLocationService: LocationListener {
    var locationViewModel: LocationViewModel? = null

    override fun onLocationChanged(newLocation: Location) {
        locationViewModel?.updateLocation(newLocation)
        Log.i("geolocation", "Location updated")
        updateLatestLocation(newLocation)
    }

    fun updateLatestLocation(latestLocation: Location) {
        locationViewModel?.updateLocation(latestLocation)
        Log.i("geolocation", "Location set to latest")
    }
}
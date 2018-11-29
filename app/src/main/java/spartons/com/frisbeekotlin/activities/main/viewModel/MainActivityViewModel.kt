package spartons.com.frisbeekotlin.activities.main.viewModel

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import spartons.com.frisbeekotlin.extensionFunction.NonNullMediatorLiveData
import spartons.com.frisbeekotlin.util.UiHelper
import kotlin.coroutines.CoroutineContext

class MainActivityViewModel constructor(
    private val uiHelper: UiHelper,
    private val locationProviderClient: FusedLocationProviderClient
) :
    ViewModel(), CoroutineScope {

    private val job = Job()
    private val _currentLocation = NonNullMediatorLiveData<Location>()
    private val _reverseGeocodeResult = NonNullMediatorLiveData<String>()

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation.apply {
                _currentLocation.postValue(this)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    val currentLocation: LiveData<Location> = _currentLocation
    val reverseGeocodeResult: LiveData<String> = _reverseGeocodeResult

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        locationProviderClient.requestLocationUpdates(
            uiHelper.getLocationRequest(),
            locationCallback,
            Looper.myLooper()
        )
    }

    fun makeReverseGeocodeRequest(latLng: LatLng, geoCoder: Geocoder) {
        launch(context = coroutineContext) {
            val result = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (result != null && result.size > 0) {
                val address = result[0]
                _reverseGeocodeResult.postValue(address.getAddressLine(0).plus(" , ").plus(address.locality))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }
}
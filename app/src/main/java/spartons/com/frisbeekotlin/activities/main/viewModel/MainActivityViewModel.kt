package spartons.com.frisbeekotlin.activities.main.viewModel

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import spartons.com.frisbeekotlin.extensionFunction.NonNullMediatorLiveData
import spartons.com.frisbeekotlin.listeners.FirebaseObjectValueListener
import spartons.com.frisbeekotlin.listeners.LatLngInterpolator
import spartons.com.frisbeekotlin.models.Driver
import spartons.com.frisbeekotlin.repo.DriverRepo
import spartons.com.frisbeekotlin.repo.MarkerRepo
import spartons.com.frisbeekotlin.util.FirebaseValueEventListenerHelper
import spartons.com.frisbeekotlin.util.GoogleMapHelper
import spartons.com.frisbeekotlin.util.MarkerAnimationHelper
import spartons.com.frisbeekotlin.util.UiHelper
import kotlin.coroutines.CoroutineContext

class MainActivityViewModel constructor(
    private val uiHelper: UiHelper,
    private val locationProviderClient: FusedLocationProviderClient,
    private val driverRepo: DriverRepo,
    private val makerRepo: MarkerRepo,
    private val googleMapHelper: GoogleMapHelper
) :
    ViewModel(), CoroutineScope, FirebaseObjectValueListener {

    private val job = SupervisorJob()
    private val _currentLocation = NonNullMediatorLiveData<Location>()
    private val _reverseGeocodeResult = NonNullMediatorLiveData<String>()
    private val _addNewMarker = NonNullMediatorLiveData<Pair<String, MarkerOptions>>()
    private val databaseReference = FirebaseDatabase.getInstance().reference.child(ONLINE_DRIVERS)

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation.apply {
                _currentLocation.postValue(this)
            }
        }
    }

    companion object {
        private const val ONLINE_DRIVERS = "online_drivers"
    }

    init {
        val firebaseValueEventListener = FirebaseValueEventListenerHelper(this)
        databaseReference.addChildEventListener(firebaseValueEventListener)
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    val currentLocation: LiveData<Location> = _currentLocation
    val reverseGeocodeResult: LiveData<String> = _reverseGeocodeResult
    val addNewMarker: LiveData<Pair<String, MarkerOptions>> = _addNewMarker

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
            try {
                val result = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                Log.e("Hello", result.toString())
                if (result != null && result.size > 0) {
                    val address = result[0]
                    _reverseGeocodeResult.postValue(address.getAddressLine(0).plus(" , ").plus(address.locality))
                }
            } catch (__: Exception) {
            }
        }
    }

    override fun onDriverOnline(driver: Driver) {
        if (driverRepo.insert(driver)) {
            val markerOptions = googleMapHelper.getDriverMarkerOptions(LatLng(driver.lat, driver.lng), driver.angle)
            _addNewMarker.value = Pair(driver.id, markerOptions)
        }
    }

    fun insertNewMarker(key: String, value: Marker) {
        makerRepo.insert(key, value)
    }

    override fun onDriverChanged(driver: Driver) {
        launch(context = coroutineContext) {
            try {
                val fetchedDriver = driverRepo.get(driver.id) ?: return@launch
                fetchedDriver.update(driver.lat, driver.lng, driver.angle)
                val marker = makerRepo.get(fetchedDriver.id) ?: return@launch
                withContext(Dispatchers.Main) {
                    marker.rotation = fetchedDriver.angle + 90
                    MarkerAnimationHelper.animateMarkerToGB(
                        marker,
                        LatLng(fetchedDriver.lat, fetchedDriver.lng),
                        LatLngInterpolator.Spherical()
                    )
                }
            } catch (__: Exception) {
            }
        }
    }

    override fun onDriverOffline(driver: Driver) {
        launch(context = coroutineContext) {
            try {
                if (driverRepo.remove(driver.id))
                    makerRepo.remove(driver.id)
            } catch (__: Exception) {
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }
}
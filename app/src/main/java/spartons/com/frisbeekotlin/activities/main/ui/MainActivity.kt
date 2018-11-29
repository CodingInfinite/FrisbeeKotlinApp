package spartons.com.frisbeekotlin.activities.main.ui

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_main.*
import spartons.com.frisbeekotlin.R
import spartons.com.frisbeekotlin.activities.BaseActivity
import spartons.com.frisbeekotlin.activities.main.viewModel.MainActivityViewModel
import spartons.com.frisbeekotlin.activities.main.viewModel.MainActivityViewModelFactory
import spartons.com.frisbeekotlin.extensionFunction.nonNull
import spartons.com.frisbeekotlin.extensionFunction.observe
import spartons.com.frisbeekotlin.listeners.IPositiveNegativeListener
import spartons.com.frisbeekotlin.listeners.LatLngInterpolator
import spartons.com.frisbeekotlin.util.GoogleMapHelper
import spartons.com.frisbeekotlin.util.MarkerAnimationHelper
import spartons.com.frisbeekotlin.util.UiHelper
import javax.inject.Inject

class MainActivity : BaseActivity(), GoogleMap.OnCameraIdleListener {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5655
    }

    @Inject
    lateinit var uiHelper: UiHelper
    @Inject
    lateinit var googleMapHelper: GoogleMapHelper

    private val geoCoderValue = lazy {
        Geocoder(this)
    }

    private var firstTimeFlag = true

    private var currentLocationMarker: Marker? = null

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getComponent().inject(this)
        val factory = MainActivityViewModelFactory(
            uiHelper,
            LocationServices.getFusedLocationProviderClient(this)
        )
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)
        if (!uiHelper.isPlayServicesAvailable()) {
            uiHelper.toast("Play services is not installed!")
            finish()
        } else
            requestLocationUpdates()
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->
            this.googleMap = googleMap
            setGoogleMapSettings()
        }
        currentLocationImageButton.setOnClickListener {
            val location = viewModel.currentLocation.value
            if (location != null)
                animateCamera(location)
        }
        viewModel.currentLocation
            .nonNull()
            .observe(this) { location ->
                if (firstTimeFlag) {
                    firstTimeFlag = false
                    animateCamera(location)
                }
                showOrAnimateMarker(location)
            }
        viewModel.reverseGeocodeResult
            .nonNull()
            .observe(this) { placeName ->
                currentPlaceTextView.text = placeName
            }
    }

    private fun requestLocationUpdates() {
        if (!uiHelper.isHaveLocationPermission()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        if (uiHelper.isLocationProviderEnabled())
            uiHelper.showPositiveDialogWithListener(
                this,
                resources.getString(R.string.need_location),
                resources.getString(R.string.location_content),
                object :
                    IPositiveNegativeListener {
                    override fun onPositive() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                },
                "Turn On",
                false
            )
        viewModel.requestLocationUpdates()
    }

    private fun setGoogleMapSettings() {
        googleMapHelper.defaultMapSettings(googleMap)
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        googleMap.setOnCameraIdleListener(this)
    }

    private fun animateCamera(location: Location) {
        val cameraUpdate = googleMapHelper.buildCameraUpdate(location)
        googleMap.animateCamera(cameraUpdate, 10, null)
    }

    private fun showOrAnimateMarker(location: Location) {
        if (currentLocationMarker == null)
            currentLocationMarker = googleMap.addMarker(googleMapHelper.getUserMarker(location))
        else
            MarkerAnimationHelper.animateMarkerToGB(currentLocationMarker, location, LatLngInterpolator.Spherical())
    }

    override fun onCameraIdle() {
        val latLng = googleMap.cameraPosition.target
        viewModel.makeReverseGeocodeRequest(latLng, geoCoderValue.value)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PERMISSION_DENIED) {
                uiHelper.showSnackBar(
                    mainActivityRootView,
                    resources.getString(R.string.frisbee_needs_your_location_in_order_to_find_your_captain_according_to_current_location)
                )
            }
            if (grantResults[0] == PERMISSION_GRANTED)
                requestLocationUpdates()
        }
    }
}

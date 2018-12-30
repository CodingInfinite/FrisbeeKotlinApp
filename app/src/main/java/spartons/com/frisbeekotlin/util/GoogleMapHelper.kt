package spartons.com.frisbeekotlin.util

import android.content.res.Resources
import android.location.Location
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.GeoApiContext
import spartons.com.frisbeekotlin.R
import javax.inject.Inject


class GoogleMapHelper @Inject constructor(private val resources: Resources) {

    companion object {
        private const val ZOOM_LEVEL = 18
        private const val TILT_LEVEL = 25
        private val geoApiContextBuilder = GeoApiContext.Builder()
    }

    /**
     * @param location in which position to Zoom the camera.
     * @return the [CameraUpdate] with Zoom and Tilt level added with the given position.
     */

    fun buildCameraUpdate(location: Location): CameraUpdate {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .tilt(TILT_LEVEL.toFloat())
            .zoom(ZOOM_LEVEL.toFloat())
            .build()
        return CameraUpdateFactory.newCameraPosition(cameraPosition)
    }

    private fun getMarkerOptions(position: LatLng, resource: Int): MarkerOptions {
        return MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(resource))
            .position(position)
    }

    fun getUserMarker(location: Location): MarkerOptions {
        return getMarkerOptions(LatLng(location.latitude, location.longitude), R.drawable.blue_dot)
    }

    /**
     * This function sets the default google map settings.
     *
     * @param googleMap to set default settings.
     */

    fun defaultMapSettings(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = true
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.isBuildingsEnabled = true
    }

    /**
     * @param position where to draw the [com.google.android.gms.maps.model.Marker]
     * @return the [MarkerOptions] with given properties added to it.
     */

    fun getDriverMarkerOptions(position: LatLng, angle: Float): MarkerOptions {
        val options = getMarkerOptions(position, R.drawable.caronmap)
        options.flat(true)
        options.rotation(angle + 90)
        return options
    }

    /**
     * @return the google distance api key.
     */

    private fun distanceApi(): String {
        return resources.getString(R.string.google_distance_matrix_api_key)
    }


    /**
     * The function returns the ${[GeoApiContext]} with distance api key.
     *
     * @return the ${[GeoApiContext]} with distance api.
     */

    fun geoContextDistanceApi(): GeoApiContext {
        return geoApiContextBuilder
            .apiKey(distanceApi())
            .build()

    }

}
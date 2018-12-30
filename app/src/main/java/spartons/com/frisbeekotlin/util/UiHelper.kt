package spartons.com.frisbeekotlin.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationRequest
import spartons.com.frisbeekotlin.R
import spartons.com.frisbeekotlin.listeners.IPositiveNegativeListener
import javax.inject.Inject


class UiHelper @Inject constructor(
    private val resources: Resources
    , private val context: Context
) {

    fun isPlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return ConnectionResult.SUCCESS == status
    }

    fun isHaveLocationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationProviderEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun showPositiveDialogWithListener(
        activity: Activity,
        title: String,
        content: String,
        positiveNegativeListener: IPositiveNegativeListener,
        positiveText: String,
        cancelable: Boolean
    ) {
        buildDialog(activity, title, content)
            .builder
            .positiveText(positiveText)
            .positiveColor(getColor(R.color.colorPrimary))
            .onPositive { _, _ -> positiveNegativeListener.onPositive() }
            .cancelable(cancelable)
            .show()
    }

    private fun buildDialog(callingClassContext: Context, title: String, content: String): MaterialDialog {
        return MaterialDialog.Builder(callingClassContext)
            .title(title)
            .content(content)
            .build()
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    fun getLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000
        return locationRequest
    }

    fun toast(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }

    fun showSnackBar(view: View, content: String) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG).show()
    }
}
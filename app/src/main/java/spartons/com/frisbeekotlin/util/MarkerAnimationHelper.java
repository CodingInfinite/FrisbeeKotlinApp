package spartons.com.frisbeekotlin.util;


import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import spartons.com.frisbeekotlin.listeners.LatLngInterpolator;

public class MarkerAnimationHelper {

    private static void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 2000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                if (t < 1)
                    handler.postDelayed(this, 16);
            }
        });
    }

    public static void animateMarkerToGB(final Marker marker, final Location finalPosition, final LatLngInterpolator latLngInterpolator) {
        animateMarkerToGB(marker, new LatLng(finalPosition.getLatitude(), finalPosition.getLongitude()), latLngInterpolator);
    }
}

package spartons.com.frisbeekotlin.collections

import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkerCollection {

    private val markerMap = mutableMapOf<String, Marker>()

    fun insertMarker(key: String, value: Marker) {
        if (!markerMap.containsKey(key))
            markerMap[key] = value
    }

    suspend fun removeMarker(key: String) = withContext(Dispatchers.Main) {
        val marker = markerMap[key]
        marker?.remove()
    }

    fun getMarker(key: String) = markerMap[key]

    fun allMarkers() = markerMap
}
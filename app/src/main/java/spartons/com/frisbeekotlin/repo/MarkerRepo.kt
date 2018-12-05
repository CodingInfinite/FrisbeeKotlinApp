package spartons.com.frisbeekotlin.repo

import com.google.android.gms.maps.model.Marker
import spartons.com.frisbeekotlin.collections.MarkerCollection
import javax.inject.Inject

class MarkerRepo @Inject constructor(private val markerCollection: MarkerCollection) {

    fun insert(key: String, marker: Marker) = markerCollection.insertMarker(key, marker)

    suspend fun remove(s: String) = markerCollection.removeMarker(s)

    fun get(s: String) = markerCollection.getMarker(s)

    fun allItems(): Map<String, Marker> {
        return markerCollection.allMarkers()
    }
}



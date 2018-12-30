package spartons.com.frisbeekotlin.collections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import spartons.com.frisbeekotlin.models.Driver
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class DriverCollection {

    private val driverList = CopyOnWriteArrayList<Driver>()

    fun insertDriver(driver: Driver) = driverList.add(driver)

    private suspend fun removeDriver(driver: Driver) = withContext(Dispatchers.Default) {
        driverList
            .filter { d ->
                d.id == driver.id
            }.map { d ->
                driverList.remove(d)
            }
    }

    suspend fun getDriverWithId(driverId: String) = withContext(Dispatchers.Default) {
        for (driver in driverList)
            if (driverId == driver.id)
                return@withContext driver
        return@withContext null
    }

    suspend fun removeDriver(driverId: String): Boolean {
        val driver = getDriverWithId(driverId)
        driver?.let {
            return@let removeDriver(it)
        }
        return false
    }

    suspend fun getNearestDriver(lat: Double, lng: Double) = withContext(Dispatchers.Default) {
        val filteredDriverList = sortDriverByDistance(lat, lng)
        return@withContext if (filteredDriverList.isEmpty())
            null
        else filteredDriverList[0]
    }

    private fun sortDriverByDistance(lat: Double, lng: Double): List<Driver> {
        val tempDrivers = mutableListOf<Driver>()
        tempDrivers.addAll(driverList)
        tempDrivers.sortWith(Comparator { driver1, driver2 ->
            val distance = measureDriverDistanceInMeters(driver1.lat, driver1.lng, lat, lng)
            val distance1 = measureDriverDistanceInMeters(driver2.lat, driver2.lng, lat, lng)
            return@Comparator distance.compareTo(distance1)
        })
        return tempDrivers
    }

    private fun measureDriverDistanceInMeters(
        driverLatitude: Double,
        driverLongitude: Double,
        currentLatitude: Double,
        currentLongitude: Double
    ): Double {
        return 1000.0 * (6371.0 * Math.acos(
            Math.cos(Math.toRadians(currentLatitude)) * Math.cos(
                Math.toRadians(
                    driverLatitude
                )
            ) * Math.cos(Math.toRadians(driverLongitude) - Math.toRadians(currentLongitude)) + Math.sin(
                Math.toRadians(
                    currentLatitude
                )
            ) * Math.sin(Math.toRadians(driverLatitude))
        ))
    }

    fun allDriver() = driverList
}


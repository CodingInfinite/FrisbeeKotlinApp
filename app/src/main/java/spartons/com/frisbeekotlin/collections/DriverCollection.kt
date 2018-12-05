package spartons.com.frisbeekotlin.collections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import spartons.com.frisbeekotlin.models.Driver
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

    fun allDriver() = driverList
}
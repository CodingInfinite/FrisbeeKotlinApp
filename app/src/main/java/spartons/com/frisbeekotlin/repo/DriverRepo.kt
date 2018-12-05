package spartons.com.frisbeekotlin.repo

import spartons.com.frisbeekotlin.collections.DriverCollection
import spartons.com.frisbeekotlin.models.Driver
import javax.inject.Inject

class DriverRepo @Inject constructor(private val driverCollection: DriverCollection) {

    fun insert(driver: Driver) = driverCollection.insertDriver(driver)

    suspend fun remove(s: String) = driverCollection.removeDriver(s)

    suspend fun get(driverId: String) = driverCollection.getDriverWithId(driverId)

    fun allItems(): List<Driver> =
        driverCollection.allDriver()
}
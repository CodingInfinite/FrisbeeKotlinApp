package spartons.com.frisbeekotlin.listeners

import spartons.com.frisbeekotlin.models.Driver

interface FirebaseObjectValueListener {

    fun onDriverOnline(driver: Driver)

    fun onDriverChanged(driver: Driver)

    fun onDriverOffline(driver: Driver)
}
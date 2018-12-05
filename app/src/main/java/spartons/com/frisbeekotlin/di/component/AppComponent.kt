package spartons.com.frisbeekotlin.di.component

import dagger.Component
import spartons.com.frisbeekotlin.di.modules.RepositoryModule
import spartons.com.frisbeekotlin.di.modules.UtilModule
import spartons.com.frisbeekotlin.di.scopes.CustomApplicationScope
import spartons.com.frisbeekotlin.repo.DriverRepo
import spartons.com.frisbeekotlin.repo.MarkerRepo
import spartons.com.frisbeekotlin.util.GoogleMapHelper
import spartons.com.frisbeekotlin.util.UiHelper

@CustomApplicationScope
@Component(modules = [UtilModule::class, RepositoryModule::class])
interface AppComponent {

    fun uiHelper(): UiHelper

    fun googleMapHelper(): GoogleMapHelper

    fun driverRepo(): DriverRepo

    fun markerRepo(): MarkerRepo
}
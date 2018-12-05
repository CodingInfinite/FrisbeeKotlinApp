package spartons.com.frisbeekotlin.di.modules

import dagger.Module
import dagger.Provides
import spartons.com.frisbeekotlin.collections.DriverCollection
import spartons.com.frisbeekotlin.collections.MarkerCollection
import spartons.com.frisbeekotlin.di.scopes.CustomApplicationScope
import spartons.com.frisbeekotlin.repo.DriverRepo
import spartons.com.frisbeekotlin.repo.MarkerRepo

@Module
class RepositoryModule {

    @Provides
    @CustomApplicationScope
    fun driverCollection() = DriverCollection()

    @Provides
    @CustomApplicationScope
    fun markerCollection() = MarkerCollection()

    @Provides
    @CustomApplicationScope
    fun driverRepo(driverCollection: DriverCollection) = DriverRepo(driverCollection)

    @Provides
    @CustomApplicationScope
    fun markerRepo(markerCollection: MarkerCollection) = MarkerRepo(markerCollection)
}
package spartons.com.frisbeekotlin.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import spartons.com.frisbeekotlin.di.qualifiers.ApplicationContextQualifier
import spartons.com.frisbeekotlin.di.scopes.CustomApplicationScope
import spartons.com.frisbeekotlin.util.GoogleMapHelper
import spartons.com.frisbeekotlin.util.UiHelper

@Module(includes = [ApplicationContextModule::class])
class UtilModule {

    @Provides
    @CustomApplicationScope
    fun uiHelper(@ApplicationContextQualifier context: Context) = UiHelper(context.resources, context)

    @Provides
    @CustomApplicationScope
    fun googleMapHelper(@ApplicationContextQualifier context: Context) = GoogleMapHelper(context.resources)
}
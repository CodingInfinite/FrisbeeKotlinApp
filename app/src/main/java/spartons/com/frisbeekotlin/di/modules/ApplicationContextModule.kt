package spartons.com.frisbeekotlin.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import spartons.com.frisbeekotlin.di.qualifiers.ApplicationContextQualifier
import spartons.com.frisbeekotlin.di.scopes.CustomApplicationScope

@Module
class ApplicationContextModule constructor(private val context: Context) {

    @Provides
    @CustomApplicationScope
    @ApplicationContextQualifier
    fun getContext() = context
}
package spartons.com.frisbeekotlin.application

import android.app.Application
import spartons.com.frisbeekotlin.di.component.AppComponent
import spartons.com.frisbeekotlin.di.component.DaggerAppComponent
import spartons.com.frisbeekotlin.di.modules.ApplicationContextModule

class MyCustomApplication : Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .applicationContextModule(ApplicationContextModule(this))
            .build()
    }

    fun appComponent() = appComponent
}
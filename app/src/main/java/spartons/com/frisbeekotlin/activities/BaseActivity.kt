package spartons.com.frisbeekotlin.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import spartons.com.frisbeekotlin.application.MyCustomApplication
import spartons.com.frisbeekotlin.di.component.ActivityComponent
import spartons.com.frisbeekotlin.di.component.DaggerActivityComponent

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = DaggerActivityComponent
            .builder()
            .appComponent(getApp().appComponent())
            .build()
    }

    protected fun getComponent() = activityComponent

    private fun getApp() = applicationContext as MyCustomApplication
}
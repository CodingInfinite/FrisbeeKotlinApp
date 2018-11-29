package spartons.com.frisbeekotlin.di.component

import dagger.Component
import spartons.com.frisbeekotlin.activities.main.ui.MainActivity
import spartons.com.frisbeekotlin.di.scopes.ActivityScope

@ActivityScope
@Component(dependencies = [AppComponent::class])
interface ActivityComponent : AppComponent {

    fun inject(mainActivity: MainActivity)
}
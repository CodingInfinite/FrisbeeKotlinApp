package spartons.com.frisbeekotlin.activities.main.viewModel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import spartons.com.frisbeekotlin.util.UiHelper

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory constructor(
    private val uiHelper: UiHelper,
    private val locationProviderClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        MainActivityViewModel(uiHelper, locationProviderClient) as T
}
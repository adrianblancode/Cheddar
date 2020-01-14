package co.adrianblan.cheddar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.adrianblan.cheddar.di.DaggerRootComponent
import co.adrianblan.cheddar.extensions.appComponent
import co.adrianblan.cheddar.feature.stories.StoriesInteractor
import co.adrianblan.stories.setupView
import javax.inject.Inject

class RootActivity : AppCompatActivity() {

    @Inject
    internal lateinit var storiesInteractor: StoriesInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerRootComponent.builder()
            .appComponent(appComponent)
            .build()
            .inject(this)

        setupView { storiesInteractor.storiesViewState }
    }

    override fun onDestroy() {
        super.onDestroy()
        storiesInteractor.onDetach()
    }
}
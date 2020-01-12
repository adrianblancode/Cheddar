package co.adrianblan.cheddar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import co.adrianblan.cheddar.feature.stories.StoriesInteractor
import co.adrianblan.cheddar.feature.stories.StoriesView
import co.adrianblan.common.ui.AppTheme

class RootActivity : AppCompatActivity() {

    private val storiesInteractor = StoriesInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                StoriesView(storiesInteractor.storiesViewState)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        storiesInteractor.onDetach()
    }
}